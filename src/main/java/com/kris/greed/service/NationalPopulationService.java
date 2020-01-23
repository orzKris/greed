package com.kris.greed.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.NationalDataConstant;
import com.kris.greed.enums.CommonConstant;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.excel.ExcelService;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.ExcelParamBean;
import com.kris.greed.model.Result;
import com.kris.greed.utils.LogUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Kris
 * @date 2019/09/13
 */
@Log4j2
@Component(ServiceCode.REGIONAL_POPULATION)
public class NationalPopulationService implements DumpService {

    private JSONObject paramJson;

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ExcelService excelService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!StringUtils.isNumeric(paramJson.getString(NationalDataConstant.YEAR))) {
            return Result.fail("year invalid !");
        }
        return Result.success();
    }

    @Override
    public boolean dump() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        List<String> columnList = new ArrayList<>();
        columnList.add(NationalDataConstant.YEAR_COLUMN);
        columnList.add(NationalDataConstant.AREA_COLUMN);
        columnList.add(NationalDataConstant.NATIONAL_POPULATION);
        columnList.add(NationalDataConstant.PERCENTAGE_OF_BIRTH);
        columnList.add(NationalDataConstant.PERCENTAGE_OF_DEATH);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        String year = paramJson.getString(NationalDataConstant.YEAR);
        List<String> paramList = new ArrayList<>();
        List<String> yearList = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        for (int i = 0; i < NationalDataConstant.districtList.size(); i++) {
            yearList.add(year);
            areaList.add(NationalDataConstant.districtList.get(i));
            paramList.add(year + NationalDataConstant.districtList.get(i) + NationalDataConstant.SEARCH_PHRASE_POPULATION);
        }
        paramMap.put(NationalDataConstant.KEY, paramList);
        LinkedHashMap<String, List<String>> excelMap = new LinkedHashMap<>();
        excelMap.put(NationalDataConstant.EXCEL_MAP_KEY_YEAR, yearList);
        excelMap.put(NationalDataConstant.EXCEL_MAP_KEY_AREA, areaList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .fileName(year + commonConfig.getNationalPopulation().getFileName())
                .sheetName(commonConfig.getNationalPopulation().getSheetName())
                .serviceIdEnum(ServiceIdEnum.D006)
                .columnList(columnList)
                .excelMap(excelMap)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        try {
            excelService.excel(excelParamBean);
            return true;
        } catch (Exception e) {
            LogUtil.logError(requestTime, "", "地区人口导出Excel失败", e);
            return false;
        }
    }

    @Override
    public List<String> dealQueryResult(JSONObject resultJson) {
        List<String> resultList = new ArrayList<>();
        JSONArray array = (JSONArray) JSONPath.eval(resultJson, "$.result.jsonResult.result");
        for (int j = 0; j < array.size(); j++) {
            JSONObject json = array.getJSONObject(j);
            if (NationalDataConstant.NATIONAL_POPULATION.equals(json.get(NationalDataConstant.DATASOURCE_KEY_1))) {
                resultList.add((String) json.get(NationalDataConstant.DATASOURCE_KEY_2));
                break;
            }
        }
        for (int j = 0; j < array.size(); j++) {
            JSONObject json = array.getJSONObject(j);
            if (NationalDataConstant.PERCENTAGE_OF_BIRTH.equals(json.get(NationalDataConstant.DATASOURCE_KEY_1))) {
                resultList.add((String) json.get(NationalDataConstant.DATASOURCE_KEY_2));
                break;
            }
        }
        for (int j = 0; j < array.size(); j++) {
            JSONObject json = array.getJSONObject(j);
            if (NationalDataConstant.PERCENTAGE_OF_DEATH.equals(json.get(NationalDataConstant.DATASOURCE_KEY_1))) {
                resultList.add((String) json.get(NationalDataConstant.DATASOURCE_KEY_2));
                break;
            }
        }
        return resultList;
    }
}
