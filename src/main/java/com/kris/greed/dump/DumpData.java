package com.kris.greed.dump;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
public class DumpData {

    @Value("${excelSize}")
    private Integer excelSize;

    @Value("${filePath}")
    private String filePath;

    @Value("${url}")
    private String url;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

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
        int index = 1;
        for (int i = 0; i <= excelSize; i++) {
            HSSFRow row = sheet.createRow(index);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue((long) (Math.random() * 10000000000L) + 10000000000L + "");
            index = index + 1;
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String mobile = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", mobile);
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
        FileOutputStream fileOutputStream = new FileOutputStream(filePath + "手机号运营商" + requestTime + ".xls");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("cost: {} ms", (System.currentTimeMillis() - start));
    }

    class MyCallable implements Callable<String> {

        private JSONObject param;

        MyCallable(JSONObject param) {
            this.param = param;
        }

        @Override
        public String call() throws IOException {
            Request request = getRequest(param);
            Call call = new OkHttpClient.Builder().callTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request);
            Response response = call.execute();
            return response.body().string();
        }
    }

    private Request getRequest(JSONObject param) throws UnsupportedEncodingException {
        return new Request.Builder()
                .url(url + "/concurrent/D005?param=" + URLEncoder.encode(param.toJSONString(), "UTF-8"))
                .post(new FormBody.Builder().build())
                .build();
    }
}
