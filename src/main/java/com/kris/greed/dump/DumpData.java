package com.kris.greed.dump;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kris.greed.constant.WzConstant;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
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
public class DumpData {

    @Value("${filePath}")
    private String filePath;

    @Value("${url}")
    private String url;

    @Value("${tokenPath}")
    private String tokenPath;

    @Value("${apiPath}")
    private String apiPath;

    @Value("${name}")
    private String name;

    @Value("${password}")
    private String password;

    private String token;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

    public void dump() throws IOException {
        long start = System.currentTimeMillis();
        Request request = getTokenRequest();
        Call call = new OkHttpClient.Builder().callTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        token = JSONObject.parseObject(responseString).getString("token");
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath + "车辆当天市民卡全部乘车记录.xls"));
        HSSFSheet sheet = workbook.getSheetAt(0);
        //设置表头
        sheet.getRow(0).getCell(0, Row.CREATE_NULL_AS_BLANK).setCellValue("交易时间");
        sheet.getRow(0).getCell(1, Row.CREATE_NULL_AS_BLANK).setCellValue("车牌");
        sheet.getRow(0).getCell(2, Row.CREATE_NULL_AS_BLANK).setCellValue("公交线路");
        sheet.getRow(0).getCell(3, Row.CREATE_NULL_AS_BLANK).setCellValue("卡号");
        sheet.getRow(0).getCell(6, Row.CREATE_NULL_AS_BLANK).setCellValue("姓名");
        sheet.getRow(0).getCell(7, Row.CREATE_NULL_AS_BLANK).setCellValue("身份证号");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String cardNo = row.getCell(3).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(WzConstant.CARD_NO, cardNo);
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
            sheet.getRow(i).getCell(6, Row.CREATE_NULL_AS_BLANK).setCellValue(getData(JSONObject.parseObject(result), WzConstant.NAME_KEY));
            sheet.getRow(i).getCell(7, Row.CREATE_NULL_AS_BLANK).setCellValue(getData(JSONObject.parseObject(result), WzConstant.ID_KEY));
            log.info("{} : {}", sheet.getRow(i).getCell(6).getStringCellValue(), sheet.getRow(i).getCell(7).getStringCellValue());
            i = i + 1;
        }
        DateFormat dateFormat = new SimpleDateFormat("M月d日");
        Date date = sheet.getRow(1).getCell(0).getDateCellValue();
        String busLine = sheet.getRow(1).getCell(2).getStringCellValue();
        String busId = sheet.getRow(1).getCell(1).getStringCellValue();
        String requestTime = dateFormat.format(date);
        FileOutputStream fileOutputStream = new FileOutputStream(filePath + requestTime + busLine + "路公交" + busId + "市民卡乘车人员身份信息" + ".xls");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("cost: {} ms", (System.currentTimeMillis() - start));
    }

    class MyCallable implements Callable<String> {

        private final JSONObject param;

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

    private Request getRequest(JSONObject param) {
        return new Request.Builder()
                .addHeader(WzConstant.AUTHORIZATION, "jwt " + token)
                .url(url + apiPath)
                .post(new FormBody.Builder()
                        .add(WzConstant.POWER_MATTERS, WzConstant.POWER_MATTERS_VALUE)
                        .add(WzConstant.SUB_POWER_MATTERS, WzConstant.SUB_POWER_MATTERS_VALUE)
                        .add(WzConstant.CARD_NO, param.getString(WzConstant.CARD_NO))
                        .build())
                .build();
    }

    private Request getTokenRequest() {
        return new Request.Builder()
                .url(url + tokenPath)
                .post(new FormBody.Builder()
                        .add(WzConstant.USERNAME, name)
                        .add(WzConstant.PASSWORD, password).build())
                .build();
    }

    private String getData(JSONObject jsonObject, String key) {
        JSONArray jsonArray = jsonObject.getJSONArray(WzConstant.DATA);
        String answer;
        if (jsonArray != null) {
            JSONObject object = jsonArray.getJSONObject(0);
            answer = object.getString(key);
        } else {
            answer = "无数据";
        }
        return answer;
    }
}
