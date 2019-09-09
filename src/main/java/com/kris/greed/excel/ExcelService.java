package com.kris.greed.excel;

import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.model.DumpService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Kris
 * @date 2019/09/09
 */
public interface ExcelService {

    void excel(ServiceIdEnum serviceIdEnum, String sheetName, List<String> columnList, LinkedHashMap<String, List<String>> paramMap, DumpService dumpService, String fileName) throws IOException;

}
