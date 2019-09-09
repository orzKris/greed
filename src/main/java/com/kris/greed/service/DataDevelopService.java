package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.excel.ExcelService;
import com.kris.greed.model.DumpService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
@Component(ServiceCode.DATA_DUMP)
public class DataDevelopService implements DumpService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ExcelService excelService;

    @Override
    public void dump() throws IOException {
        List<String> columnList = new ArrayList<>();
        columnList.add("接口编号");
        columnList.add("调用量");
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i <= commonConfig.getDataDevelopment().getExcelSize(); i++) {
            paramList.add(getInterfaceId(i));
        }
        paramMap.put("interfaceId", paramList);
        excelService.excel(ServiceIdEnum.D000, commonConfig.getDataDevelopment().getSheetName(), columnList, paramMap, this, commonConfig.getDataDevelopment().getFileName());
    }

    @Override
    public String dealQueryResult(JSONObject resultJson) {
        return JSONPath.eval(resultJson, "$.result.jsonResult.count") + "";
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

}