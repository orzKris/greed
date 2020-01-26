package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.kris.greed.config.CommonConfig;
import com.kris.greed.constant.TaoBaoBeautyConstant;
import com.kris.greed.enums.*;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Component(ServiceCode.TAO_BAO_BEAUTY)
public class TaoBaoBeautyService implements DumpService {

    private JSONObject paramJson;

    /**
     * 存储计数标识
     */
    private static final ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

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
        if (!BeautyTypeEnum.hasType(paramJson.getString(TaoBaoBeautyConstant.TYPE))) {
            return Result.fail("type invalid !");
        }
        if (paramJson.containsKey(TaoBaoBeautyConstant.PAGE)) {
            if (!StringUtils.isNumeric(paramJson.getString(TaoBaoBeautyConstant.PAGE))) {
                return Result.fail("page invalid !");
            }
        }
        return Result.success();
    }

    /**
     * 该接口重复请求同一接口20次
     */
    @Override
    public Result dump() {
        String type = paramJson.getString(TaoBaoBeautyConstant.TYPE);
        String page = paramJson.getString(TaoBaoBeautyConstant.PAGE);
        String typeDesc = BeautyTypeEnum.getTypeDesc(type);
        List<String> columnList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        columnList.add(TaoBaoBeautyConstant.TYPE_COLUMN);
        columnList.add(TaoBaoBeautyConstant.NICKNAME);
        columnList.add(TaoBaoBeautyConstant.CITY);
        columnList.add(TaoBaoBeautyConstant.AVATAR);
        columnList.add(TaoBaoBeautyConstant.LINK);
        columnList.add(TaoBaoBeautyConstant.HEIGHT);
        columnList.add(TaoBaoBeautyConstant.WEIGHT);
        columnList.add(TaoBaoBeautyConstant.PHOTO);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        LinkedHashMap<String, List<String>> excelMap = new LinkedHashMap<>();
        List<String> typeList = new ArrayList<>();
        List<String> pageList = new ArrayList<>();
        List<String> typeDescList = new ArrayList<>();
        for (int i = 0; i <= TaoBaoBeautyConstant.PAGE_MAX_SIZE; i++) {
            typeList.add(type);
            pageList.add(page);
            typeDescList.add(typeDesc);
        }
        paramMap.put(TaoBaoBeautyConstant.TYPE, typeList);
        paramMap.put(TaoBaoBeautyConstant.PAGE, pageList);
        excelMap.put("typeDesc", typeDescList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .sheetName(commonConfig.getTaobaoBeauty().getSheetName())
                .fileName(typeDesc + commonConfig.getTaobaoBeauty().getFileName() + "0" + page + ".xls")
                .serviceIdEnum(ServiceIdEnum.D007)
                .columnList(columnList)
                .excelMap(excelMap)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        try {
            JSONObject resultJson = excelService.excel(excelParamBean);
            return new Result(DataErrorCode.SUCCESS, resultJson);
        } catch (Exception e) {
            LogUtil.logError(requestTime, "", "淘女郎查询导出Excel失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    @Override
    public List<String> dealQueryResult(JSONObject resultJson) {
        List<String> resultList = new ArrayList<>();
        List<JSONObject> contentList;
        contentList = (List<JSONObject>) JSONPath.eval(resultJson, "$.result.jsonResult.showapi_res_body.pagebean.contentlist");
        Integer index = map.get("count");
        if (index == null) {
            index = 0;
            map.put("count", index + 1);
        } else if (index == TaoBaoBeautyConstant.PAGE_MAX_SIZE) {
            map.remove("count");
        } else {
            map.put("count", index + 1);
        }
        JSONObject elementJson = contentList.get(index);
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_NICKNAME));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_CITY));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_AVATAR));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_LINK));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_HEIGHT));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_WEIGHT));
        resultList.add(elementJson.getString(TaoBaoBeautyConstant.DATASOURCE_PHOTO));
        return resultList;
    }
}
