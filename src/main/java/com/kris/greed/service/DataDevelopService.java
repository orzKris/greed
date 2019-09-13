package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.DataDevelopConstant;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.excel.ExcelService;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.ExcelParamBean;
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
        columnList.add(DataDevelopConstant.INTERFACE_COLUMN);
        columnList.add(DataDevelopConstant.INVOCATION_VOLUME);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i <= commonConfig.getDataDevelopment().getExcelSize(); i++) {
            paramList.add(getInterfaceId(i));
        }
        paramMap.put(DataDevelopConstant.INTERFACE_ID, paramList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .sheetName(commonConfig.getDataDevelopment().getSheetName())
                .fileName(commonConfig.getDataDevelopment().getFileName())
                .serviceIdEnum(ServiceIdEnum.D000)
                .columnList(columnList)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        excelService.excel(excelParamBean);
    }

    @Override
    public List<String> dealQueryResult(JSONObject resultJson) {
        List<String> resultList = new ArrayList<>();
        resultList.add(JSONPath.eval(resultJson, "$.result.jsonResult.count") + "");
        return resultList;
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
