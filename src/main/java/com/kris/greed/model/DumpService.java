package com.kris.greed.model;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

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
    List<String> dealQueryResult(JSONObject resultJson);
}
