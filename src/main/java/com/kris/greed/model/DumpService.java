package com.kris.greed.model;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * @author by Kris
 * @date 2019/08/27
 */
public interface DumpService {

    void dump() throws IOException;

    /**
     * callback method
     *
     * @param resultJson prophecy response
     * @return finalResult
     */
    String dealQueryResult(JSONObject resultJson);
}
