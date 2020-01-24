package com.kris.greed.excel;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.model.ExcelParamBean;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/09/09
 */
public interface ExcelService {

    JSONObject excel(ExcelParamBean excelParamBean) throws IOException;

}
