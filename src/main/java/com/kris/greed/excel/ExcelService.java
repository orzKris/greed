package com.kris.greed.excel;

import com.kris.greed.model.ExcelParamBean;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/09/09
 */
public interface ExcelService {

    void excel(ExcelParamBean excelParamBean) throws IOException;

}
