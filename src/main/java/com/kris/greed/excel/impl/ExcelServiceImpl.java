package com.kris.greed.excel.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.excel.ExcelService;
import com.kris.greed.feign.ProphecyService;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.ProphecyCaller;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Kris
 * @date 2019/09/09
 */
@Log4j2
@Component
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ProphecyService prophecyService;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

    @Override
    public void excel(ServiceIdEnum serviceIdEnum, String sheetName, List<String> columnList, LinkedHashMap<String, List<String>> paramMap, DumpService dumpService, String fileName) throws IOException {
        long startTime = System.currentTimeMillis();
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        HSSFRow beginRow = sheet.createRow(0);
        //第一行属性名称,顺序从左到右
        for (int i = 0; i < columnList.size(); i++) {
            HSSFCell cell = beginRow.createCell(i);
            cell.setCellValue(columnList.get(i));
        }
        //写入参数
        int index = 0;
        for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
            List<String> paramList = entry.getValue();
            for (int i = 1; i <= paramList.size(); i++) {
                HSSFRow row = sheet.createRow(i);
                HSSFCell cell = row.createCell(index);
                cell.setCellValue(paramList.get(i - 1));
            }
            index = index + 1;
        }
        //提交任务
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            JSONObject paramJson = new JSONObject();
            int j = 0;
            for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
                paramJson.put(entry.getKey(), row.getCell(j).getStringCellValue());
                j = j + 1;
            }
            ProphecyCaller callable = new ProphecyCaller(paramJson, serviceIdEnum, prophecyService);
            futureList.add(threadPool.submit(callable));
        }
        //写入结果
        int i = 1;
        for (Future future : futureList) {
            String result = null;
            try {
                result = (String) future.get();
            } catch (Exception e) {
                log.error("thread pool task error", e);
            }
            JSONObject resultJson = JSON.parseObject(result);
            String finalResult = dumpService.dealQueryResult(resultJson);
            HSSFRow row = sheet.getRow(i);
            row.getCell(columnList.size() - 1, Row.CREATE_NULL_AS_BLANK).setCellValue(finalResult);
            setLog(row, finalResult, columnList.size() - 1);
            i = i + 1;
        }
        toFile(workbook, fileName);
        log.info("cost: {} ms", (System.currentTimeMillis() - startTime));
    }

    private void setLog(HSSFRow row, String finalResult, int size) {
        StringBuilder logString = new StringBuilder();
        for (int i = 0; i < size; i++) {
            logString.append(row.getCell(i).getStringCellValue());
        }
        log.info("{} : {}", logString.toString(), finalResult);
    }

    private void toFile(HSSFWorkbook hssfWorkbook, String fileName) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String requestTime = dateFormat.format(new Date());
        FileOutputStream fileOutputStream = new FileOutputStream(commonConfig.getFilePath() + fileName + requestTime + ".xls");
        hssfWorkbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}