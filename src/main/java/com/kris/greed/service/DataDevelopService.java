package com.kris.greed.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.feign.ProphecyService;
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
@Component
public class DataDevelopService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ProphecyService prophecyService;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

    public void dump() throws IOException {
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String requestTime = dateFormat.format(new Date());
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("调用量统计");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        HSSFCell beginCell1 = beginRow.createCell(1);
        beginCell0.setCellValue("接口编号");
        beginCell1.setCellValue("调用量");
        int index = 1;
        for (int i = 0; i <= commonConfig.getDataDevelopment().getExcelSize(); i++) {
            HSSFRow row = sheet.createRow(index);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(getInterfaceId(i));
            index = index + 1;
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String interfaceId = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("interfaceId", interfaceId);
            MyCallable callable = new MyCallable(jsonObject);
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

            JSONObject resultJson = JSON.parseObject(result);
            int count = (int) JSONPath.eval(resultJson, "$.result.jsonResult.count");
            sheet.getRow(i).getCell(1, Row.CREATE_NULL_AS_BLANK).setCellValue(count);
            log.info("{} : {}", sheet.getRow(i).getCell(0).getStringCellValue(), count);
            i = i + 1;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(commonConfig.getFilePath() + "prophecy调用量统计" + requestTime + ".xls");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("cost: {} ms", (System.currentTimeMillis() - start));
    }

    private String getInterfaceId(Integer i) {
        if (i >= 0 && i < 10) {
            return "D00" + i;
        } else if (i < 100) {
            return "D0" + i;
        } else if (i < 1000) {
            return "D" + i;
        } else {
            throw new RuntimeException("interfaceId out of range");
        }
    }

    class MyCallable implements Callable<String> {

        private JSONObject param;

        MyCallable(JSONObject param) {
            this.param = param;
        }

        @Override
        public String call() {
            return prophecyService.call(ServiceIdEnum.D000.getId(), param.toJSONString());
        }
    }
}
