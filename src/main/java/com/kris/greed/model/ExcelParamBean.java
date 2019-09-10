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

    private List<String> columnList;

    private LinkedHashMap<String, List<String>> paramMap;

    private DumpService dumpService;

}
