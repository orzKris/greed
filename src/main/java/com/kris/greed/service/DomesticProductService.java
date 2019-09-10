package com.kris.greed.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.DomesticProductConstant;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.excel.ExcelService;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.ExcelParamBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author by Kris
 * @date 2019/09/08
 */
@Log4j2
@Component(ServiceCode.GDP)
public class DomesticProductService implements DumpService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ExcelService excelService;

    @Override
    public void dump() throws IOException {
        List<String> columnList = new ArrayList<>();
        columnList.add(DomesticProductConstant.YEAR_COLUMN);
        columnList.add(DomesticProductConstant.AREA_COLUMN);
        columnList.add(DomesticProductConstant.DOMESTIC_PRODUCT);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        String year = commonConfig.getDomesticProduct().getYear() + "";
        List<String> yearList = new ArrayList<>();
        for (int i = 1; i <= DomesticProductConstant.districtList.size(); i++) {
            yearList.add(year);
        }
        paramMap.put(DomesticProductConstant.YEAR, yearList);
        paramMap.put(DomesticProductConstant.AREA, DomesticProductConstant.districtList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .fileName(commonConfig.getDomesticProduct().getYear() + commonConfig.getDomesticProduct().getFileName())
                .sheetName(commonConfig.getDomesticProduct().getSheetName())
                .serviceIdEnum(ServiceIdEnum.D006)
                .columnList(columnList)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        excelService.excel(excelParamBean);
    }

    @Override
    public String dealQueryResult(JSONObject resultJson) {
        String product = "";
        JSONArray array = (JSONArray) JSONPath.eval(resultJson, "$.result.jsonResult.result");
        for (int j = 0; j < array.size(); j++) {
            JSONObject json = array.getJSONObject(j);
            if (DomesticProductConstant.DATASOURCE_VALUE_1.equals(json.get(DomesticProductConstant.DATASOURCE_KEY_1))
                    || DomesticProductConstant.DATASOURCE_VALUE_2.equals(json.get(DomesticProductConstant.DATASOURCE_KEY_1))) {
                product = (String) json.get(DomesticProductConstant.DATASOURCE_KEY_2);
                break;
            }
        }
        return product;
    }

}
