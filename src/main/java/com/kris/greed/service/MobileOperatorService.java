package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.MobileOperatorConstant;
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
@Component(ServiceCode.MOBILE_LOCATION)
public class MobileOperatorService implements DumpService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ExcelService excelService;

    @Override
    public void dump() throws IOException {
        List<String> columnList = new ArrayList<>();
        columnList.add(MobileOperatorConstant.MOBILE_COLUMN);
        columnList.add(MobileOperatorConstant.OPERATOR_COLUMN);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i <= commonConfig.getMobileOperator().getExcelSize(); i++) {
            paramList.add((long) (Math.random() * 10000000000L) + 10000000000L + "");
        }
        paramMap.put(MobileOperatorConstant.MOBILE, paramList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .sheetName(commonConfig.getMobileOperator().getSheetName())
                .fileName(commonConfig.getMobileOperator().getFileName())
                .serviceIdEnum(ServiceIdEnum.D005)
                .columnList(columnList)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        excelService.excel(excelParamBean);
    }

    @Override
    public String dealQueryResult(JSONObject resultJson) {
        String result = resultJson.toJSONString();
        String operator;
        try {
            operator = result.substring(result.indexOf(MobileOperatorConstant.BEGIN_STRING), result.indexOf(MobileOperatorConstant.END_STRING));
            operator = operator.substring(9);
        } catch (Exception e) {
            operator = MobileOperatorConstant.SPACE_NUMBER;
        }
        return operator;
    }
}
