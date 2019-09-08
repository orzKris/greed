package com.kris.greed.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.feign.ProphecyService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author by Kris
 * @date 2019/09/08
 */
@Log4j2
@Component(ServiceCode.GDP)
public class DomesticProductService {

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
        HSSFSheet sheet = workbook.createSheet("地区生产总值");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        HSSFCell beginCell1 = beginRow.createCell(1);
        HSSFCell beginCell2 = beginRow.createCell(2);
        beginCell0.setCellValue("年份");
        beginCell1.setCellValue("地区");
        beginCell2.setCellValue("生产总值(亿元)");
        int index = 1;
        for (int i = 0; i <= districtList.size(); i++) {
            HSSFRow row = sheet.createRow(index);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(commonConfig.getDomesticProduct().getYear());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(districtList.get(i));
            index = index + 1;
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String year = row.getCell(0).getStringCellValue();
            String district = row.getCell(1).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("year", year);
            jsonObject.put("area", district);
            ProphecyCaller callable = new ProphecyCaller(jsonObject, ServiceIdEnum.D006, prophecyService);
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

            String product = "";
            JSONObject resultJson = JSON.parseObject(result);
            JSONArray array = (JSONArray) JSONPath.eval(resultJson, "$.result.jsonResult.result");
            for (int j = 0; j < array.size(); j++) {
                JSONObject json = array.getJSONObject(j);
                if ("国内生产总值(亿元)".equals(json.get("zb"))) {
                    product = (String) json.get("data");
                    break;
                }
            }
            sheet.getRow(i).getCell(2, Row.CREATE_NULL_AS_BLANK).setCellValue(product);
            log.info("{} {} : {}", sheet.getRow(i).getCell(0).getStringCellValue(),
                    sheet.getRow(i).getCell(1).getStringCellValue(), product);
            i = i + 1;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(commonConfig.getFilePath()
                + commonConfig.getDomesticProduct().getYear() + "全国各地区生产总值" + requestTime + ".xls");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("cost: {} ms", (System.currentTimeMillis() - start));
    }

    private static List<String> districtList = Arrays.asList("河北", "山西", "内蒙古自治区", "黑龙江", "吉林", "辽宁",
            "陕西", "甘肃", "青海", "新疆维吾尔自治", "宁夏回族自治区", "山东", "河南", "江苏", "浙江", "安徽", "江西",
            "福建", "湖北", "湖南", "广东", "广西壮族自治区", "海南", "四川", "云南", "贵州", "西藏自治区",
            "北京", "上海", "天津", "重庆");
}
