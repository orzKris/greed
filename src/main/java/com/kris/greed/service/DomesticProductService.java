package com.kris.greed.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.NationalDataConstant;
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
        columnList.add(NationalDataConstant.YEAR_AREA_COLUMN);
        columnList.add(NationalDataConstant.DOMESTIC_PRODUCT);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        String year = commonConfig.getDomesticProduct().getYear() + "";
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < NationalDataConstant.districtList.size(); i++) {
            paramList.add(year + NationalDataConstant.districtList.get(i) + NationalDataConstant.SEARCH_PHRASE_1);
        }
        paramMap.put(NationalDataConstant.KEY, paramList);
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
    public List<String> dealQueryResult(JSONObject resultJson) {
        List<String> resultList = new ArrayList<>();
        JSONArray array = (JSONArray) JSONPath.eval(resultJson, "$.result.jsonResult.result");
        for (int j = 0; j < array.size(); j++) {
            JSONObject json = array.getJSONObject(j);
            if (NationalDataConstant.DATASOURCE_VALUE_1.equals(json.get(NationalDataConstant.DATASOURCE_KEY_1))
                    || NationalDataConstant.DATASOURCE_VALUE_2.equals(json.get(NationalDataConstant.DATASOURCE_KEY_1))) {
                resultList.add((String) json.get(NationalDataConstant.DATASOURCE_KEY_2));
                break;
            }
        }
        return resultList;
    }

}
