package com.kris.greed.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.greed.config.ApplicationContextRegister;
import com.kris.greed.enums.CommonConstant;
import com.kris.greed.enums.DataErrorCode;
import com.kris.greed.exception.ExcelGenerateException;
import com.kris.greed.exception.ParamErrorException;
import com.kris.greed.model.CallMap;
import com.kris.greed.model.DumpService;
import com.kris.greed.model.Result;
import com.kris.greed.utils.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void main(HttpServletResponse response, @PathVariable("id") String id, @RequestParam("param") String param) throws IOException {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        Result paramResult = checkParam(param, requestTime);
        if (paramResult.getStatus() != DataErrorCode.SUCCESS) {
            throw new ParamErrorException(paramResult.getStatus().getErrorMsg());
        }
        String serviceName = callMap.getMap().get(id);
        if (serviceName == null) {
            throw new ParamErrorException(DataErrorCode.NO_CONFIGURED_SERVICE.getErrorMsg());
        }
        LogUtil.logInfo("调用的服务有：" + serviceName);
        DumpService dumpService = (DumpService) applicationContextRegister.getApplicationContext().getBean(serviceName);
        dumpService.init(paramResult.getJsonResult());
        Result checkResult = dumpService.checkParam(paramResult.getJsonResult());
        if (DataErrorCode.PARAM_ERROR.equals(checkResult.getStatus())) {
            checkResult.setName(serviceName);
            throw new ParamErrorException(checkResult.getJsonResult().getString("response"));
        }
        Result result = dumpService.dump();
        if (result.getStatus() == DataErrorCode.FAIL) {
            throw new ExcelGenerateException();
        }
        //这后面可以设置导出Excel的名称
        String filename = (String) result.getJsonResult().keySet().toArray()[0];
        //这样设置filename，若其中有中文也不会乱码
        response.setHeader("Content-disposition", "attachment;filename=" + filename +";filename*=utf-8''"+ URLEncoder.encode(filename ,"UTF-8"));
        //刷新缓冲
        response.flushBuffer();
        //workbook将Excel写入到response的输出流中，供页面下载
        HSSFWorkbook workbook = (HSSFWorkbook) result.getJsonResult().get(filename);
        workbook.write(response.getOutputStream());
    }

    private Result checkParam(String param, String requestTime) {
        if (StringUtils.isBlank(param)) {
            return new Result(DataErrorCode.PARAM_ERROR);
        }
        param = param.replaceAll(" ", "");

        try {
            return new Result(DataErrorCode.SUCCESS, JSONObject.parseObject(param));
        } catch (Exception e) {
            LogUtil.logError(requestTime, param, CommonConstant.PARAM_MUST_BE_JSON, e);
            return new Result(DataErrorCode.PARAM_ERROR);
        }
    }
}
