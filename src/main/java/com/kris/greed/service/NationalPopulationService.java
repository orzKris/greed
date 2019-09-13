package com.kris.greed.service;

import com.alibaba.fastjson.JSONObject;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Kris
 * @date 2019/09/13
 */
@Log4j2
@Component(ServiceCode.REGIONAL_POPULATION)
public class NationalPopulationService implements DumpService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private ExcelService excelService;

    @Override
    public void dump() throws IOException {
        List<String> columnList = new ArrayList<>();
        columnList.add(NationalDataConstant.YEAR_AREA_COLUMN);
        columnList.add(NationalDataConstant.NATIONAL_POPULATION);
        LinkedHashMap<String, List<String>> paramMap = new LinkedHashMap<>();
        String year = commonConfig.getNationalPopulation().getYear() + "";
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < NationalDataConstant.districtList.size(); i++) {
            paramList.add(year + NationalDataConstant.districtList.get(i) + NationalDataConstant.SEARCH_PHRASE_2);
        }
        paramMap.put(NationalDataConstant.KEY, paramList);
        ExcelParamBean excelParamBean = ExcelParamBean.builder()
                .fileName(commonConfig.getNationalPopulation().getYear() + commonConfig.getNationalPopulation().getFileName())
                .sheetName(commonConfig.getNationalPopulation().getSheetName())
                .serviceIdEnum(ServiceIdEnum.D006)
                .columnList(columnList)
                .paramMap(paramMap)
                .dumpService(this)
                .build();
        excelService.excel(excelParamBean);
    }

    @Override
    public List<String> dealQueryResult(JSONObject resultJson) {
        return null;
    }
}
