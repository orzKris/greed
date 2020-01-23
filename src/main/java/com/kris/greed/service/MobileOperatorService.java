package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.MobileOperatorConstant;
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
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
@Component(ServiceCode.MOBILE_LOCATION)
public class MobileOperatorService implements DumpService {

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
        if (!StringUtils.isNumeric(paramJson.getString(MobileOperatorConstant.EXCEL_SIZE))) {
            return Result.fail("excel_size invalid !");
        }
        return Result.success();
    }

    @Override
    public boolean dump() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        List<String> columnList = new ArrayList<>();
        columnList.add(MobileOperatorConstant.MOBILE_COLUMN);
        columnList.add(MobileOperatorConstant.OPERATOR_COLUMN);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i <= paramJson.getInteger(MobileOperatorConstant.EXCEL_SIZE); i++) {
            paramList.add((long) (Math.random() * 10000000000L) + 10000000000L + "");
        }
        paramMap.put(MobileOperatorConstant.MOBILE, paramList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .sheetName(commonConfig.getMobileOperator().getSheetName())
                .fileName(commonConfig.getMobileOperator().getFileName())
                .serviceIdEnum(ServiceIdEnum.D005)
                .columnList(columnList)
                .excelMap(paramMap)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        try {
            excelService.excel(excelParamBean);
            return true;
        } catch (Exception e) {
            LogUtil.logError(requestTime, "", "手机归属地导出Excel失败", e);
            return false;
        }
    }

    @Override
    public List<String> dealQueryResult(JSONObject resultJson) {
        String result = resultJson.toJSONString();
        List<String> resultList = new ArrayList<>();
        try {
            resultList.add(result.substring(result.indexOf(MobileOperatorConstant.BEGIN_STRING), result.indexOf(MobileOperatorConstant.END_STRING)).substring(9));
        } catch (Exception e) {
            resultList.add(MobileOperatorConstant.SPACE_NUMBER);
        }
        return resultList;
    }
}
