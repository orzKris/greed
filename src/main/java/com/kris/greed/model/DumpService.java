package com.kris.greed.model;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author by Kris
 * @date 2019/08/27
 */
public interface DumpService {

    /**
     * 初始化传参
     */
    void init(JSONObject paramJson);

    /**
     * 参数检查
     */
    Result checkParam(JSONObject paramJson);

    /**
     * 导出Excel
     */
    Result dump();

    /**
     * callback method
     *
     * @param resultJson prophecy response
     * @return finalResult
     */
    List<String> dealQueryResult(JSONObject resultJson);
}
