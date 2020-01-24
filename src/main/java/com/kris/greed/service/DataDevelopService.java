package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.DataDevelopConstant;
import com.kris.greed.constant.MobileOperatorConstant;
import com.kris.greed.enums.CommonConstant;
import com.kris.greed.enums.DataErrorCode;
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
@Component(ServiceCode.DATA_DUMP)
public class DataDevelopService implements DumpService {

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
        if (!StringUtils.isNumeric(paramJson.getString(DataDevelopConstant.EXCEL_SIZE))) {
            return Result.fail("excel_size invalid !");
        }
        return Result.success();
    }

    @Override
    public Result dump() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        List<String> columnList = new ArrayList<>();
        columnList.add(DataDevelopConstant.INTERFACE_COLUMN);
        columnList.add(DataDevelopConstant.INVOCATION_VOLUME);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i <= paramJson.getInteger(DataDevelopConstant.EXCEL_SIZE); i++) {
            paramList.add(getInterfaceId(i));
        }
        paramMap.put(DataDevelopConstant.INTERFACE_ID, paramList);
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT);
        String fileTime = dateFormat.format(new Date());
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .sheetName(commonConfig.getDataDevelopment().getSheetName())
                .fileName(commonConfig.getDataDevelopment().getFileName() + fileTime + ".xls")
                .serviceIdEnum(ServiceIdEnum.D000)
                .columnList(columnList)
                .excelMap(paramMap)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        try {
            JSONObject resultJson = excelService.excel(excelParamBean);
            return new Result(DataErrorCode.SUCCESS, resultJson);
        } catch (Exception e) {
            LogUtil.logError(requestTime, "", "大数据输出统计数据导出Excel失败", e);
            return new Result(DataErrorCode.FAIL);
        }
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
