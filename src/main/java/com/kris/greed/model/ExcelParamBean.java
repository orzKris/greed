package com.kris.greed.model;

import com.kris.greed.enums.ServiceIdEnum;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@Builder
public class ExcelParamBean {

    private ServiceIdEnum serviceIdEnum;

    private String fileName;

    private String sheetName;

    /**
     * Excel第一行的中文属性名称
     */
    private List<String> columnList;

    /**
     * 用于直接请求prophecy的参数项
     */
    private LinkedHashMap<String, List<String>> paramMap;

    /**
     * 填入Excel的参数项，该参数不会用于直接请求prophecy，而是用于Excel数据展示
     */
    private LinkedHashMap<String, List<String>> excelMap;

    private DumpService dumpService;

}
