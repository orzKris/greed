package com.kris.greed.dump;

import com.alibaba.fastjson.JSONArray;
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

import java.io.FileInputStream;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String requestTime = dateFormat.format(new Date());
        Request request = getTokenRequest();
        Call call = new OkHttpClient.Builder().callTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        token = JSONObject.parseObject(responseString).getString("token");
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream("C:\\Users\\Administrator\\Desktop\\车辆当天市民卡全部乘车记录.xls"));
        HSSFSheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String cardNo = row.getCell(3).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("p_cardId", cardNo);
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

//            String operator;
//            try {
//                operator = result.substring(result.indexOf("carrier:'"), result.indexOf("'\\n}"));
//                operator = operator.substring(9);
//            } catch (Exception e) {
//                operator = "空号";
//            }
            sheet.getRow(i).getCell(6, Row.CREATE_NULL_AS_BLANK).setCellValue(getData(JSONObject.parseObject(result), "AAC003"));
            sheet.getRow(i).getCell(7, Row.CREATE_NULL_AS_BLANK).setCellValue(getData(JSONObject.parseObject(result), "AAC147"));
            log.info("{} : {}", sheet.getRow(i).getCell(6).getStringCellValue(), sheet.getRow(i).getCell(7).getStringCellValue());
            i = i + 1;
        }
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\车辆当天市民卡全部乘车记录" + requestTime + ".xls");
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

    private Request getRequest(JSONObject param) {
        return new Request.Builder()
                .addHeader("Authorization", "jwt " + token)
                .url(url + apiPath)
                .post(new FormBody.Builder()
                        .add("powermatters", "95a04432-e8c2-4666-84dd-98979df1729d")
                        .add("subpowermatters", "许可-00193-001-02")
                        .add("p_cardId", param.getString("p_cardId"))
                        .build())
                .build();
    }

    private Request getTokenRequest() {
        return new Request.Builder()
                .url(url + tokenPath)
                .post(new FormBody.Builder()
                        .add("username", name)
                        .add("password", password).build())
                .build();
    }

    private String getData(JSONObject jsonObject, String key) {
        JSONArray jsonArray = jsonObject.getJSONArray("datas");
        JSONObject answer = jsonArray.getJSONObject(0);
        return answer.getString(key);
    }
}
