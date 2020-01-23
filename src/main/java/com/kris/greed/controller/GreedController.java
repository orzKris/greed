package com.kris.greed.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.ApplicationContextRegister;
import com.kris.greed.enums.CommonConstant;
import com.kris.greed.enums.DataErrorCode;
import com.kris.greed.model.CallMap;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.common.util.Response;
import com.kris.greed.utils.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris on 2020/1/22
 */
@RequestMapping(value = "/greed")
@RestController
@Scope("request")
public class GreedController {

    @Autowired
    private CallMap callMap;

    @Autowired
    private ApplicationContextRegister applicationContextRegister;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response main(@PathVariable("id") String id, @RequestParam("param") String param) throws IOException {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        Response paramResult = checkParam(param, requestTime);
        if (!paramResult.getResponseCode().equals(DataErrorCode.SUCCESS.getCode())) {
            return paramResult;
        }
        String serviceName = callMap.getMap().get(id);
        if (serviceName == null) {
            return Response.error(DataErrorCode.NO_CONFIGURED_SERVICE);
        }
        LogUtil.logInfo("调用的服务有：" + serviceName);
        DumpService dumpService = (DumpService) applicationContextRegister.getApplicationContext().getBean(serviceName);
        dumpService.init((JSONObject) paramResult.getResult());
        boolean flag = dumpService.dump();
        if (flag) {
            return Response.message(DataErrorCode.SUCCESS.getErrorMsg());
        } else {
            return Response.message(DataErrorCode.FAIL.getErrorMsg());
        }
    }

    private Response checkParam(String param, String requestTime) {
        if (StringUtils.isBlank(param)) {
            return Response.error(DataErrorCode.PARAM_ERROR);
        }
        param = param.replaceAll(" ", "");

        try {
            return Response.ok(JSONObject.parseObject(param));
        } catch (Exception e) {
            LogUtil.logError(requestTime, param, CommonConstant.PARAM_MUST_BE_JSON, e);
            return Response.error(DataErrorCode.PARAM_ERROR);
        }
    }
}
