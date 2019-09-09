package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
@Component(ServiceCode.MOBILE_LOCATION)
public class MobileOperatorService implements DumpService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ProphecyService prophecyService;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

    @Override
    public void dump() throws IOException {
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String requestTime = dateFormat.format(new Date());
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("手机号运营商");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        HSSFCell beginCell1 = beginRow.createCell(1);
        beginCell0.setCellValue("手机号");
        beginCell1.setCellValue("运营商");
        for (int i = 0; i < commonConfig.getMobileOperator().getExcelSize(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue((long) (Math.random() * 10000000000L) + 10000000000L + "");
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String mobile = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", mobile);
            ProphecyCaller callable = new ProphecyCaller(jsonObject, ServiceIdEnum.D005, prophecyService);
            futureList.add(threadPool.submit(callable));
        }

        int i = 1;
        for (Future future : futureList) {
            String result = null;
            try {
                result = (String) future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String operator;
            try {
                operator = result.substring(result.indexOf("carrier:'"), result.indexOf("'\\n}"));
                operator = operator.substring(9);
            } catch (Exception e) {
                operator = "空号";
            }
            sheet.getRow(i).getCell(1, Row.CREATE_NULL_AS_BLANK).setCellValue(operator);
            log.info("{} : {}", sheet.getRow(i).getCell(0).getStringCellValue(), operator);
            i = i + 1;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(commonConfig.getFilePath() + "手机号运营商" + requestTime + ".xls");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("cost: {} ms", (System.currentTimeMillis() - start));
    }

    @Override
    public String dealQueryResult(JSONObject resultJson) {
        return null;
    }
}
