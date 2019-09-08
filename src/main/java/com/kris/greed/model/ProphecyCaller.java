package com.kris.greed.model;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.enums.ServiceIdEnum;
import com.kris.greed.feign.ProphecyService;

import java.util.concurrent.Callable;

/**
 * @author Kris
 * @date 2019/09/02
 */
public class ProphecyCaller implements Callable {

    private ProphecyService prophecyService;

    private JSONObject param;

    private ServiceIdEnum serviceIdEnum;

    public ProphecyCaller(JSONObject param, ServiceIdEnum serviceIdEnum, ProphecyService prophecyService) {
        this.param = param;
        this.serviceIdEnum = serviceIdEnum;
        this.prophecyService = prophecyService;
    }

    @Override
    public Object call() {
        return prophecyService.call(serviceIdEnum.getId(), param.toJSONString());
    }
}
