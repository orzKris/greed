package com.kris.greed.model;


import com.alibaba.fastjson.JSONObject;
import com.kris.greed.enums.DataErrorCode;
import lombok.Data;

/**
 * @author Kris
 * @date 2018/11/29
 */
@Data
public class Result {

    private String name;

    private DataErrorCode status;

    private JSONObject jsonResult;

    public Result() {
    }

    public Result(DataErrorCode status) {
        this.status = status;
    }

    public Result(DataErrorCode status, JSONObject jsonResult) {
        this.status = status;
        this.jsonResult = jsonResult;
    }

    public Result(String name, DataErrorCode status) {
        this.name = name;
        this.status = status;
    }

    public static Result success() {
        Result result = new Result(DataErrorCode.SUCCESS);
        return result;
    }

    public static Result fail(String s) {
        Result result = new Result();
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("response", s);
        result.setStatus(DataErrorCode.PARAM_ERROR);
        result.setJsonResult(jsonResult);
        return result;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("jsonResult", jsonResult);
        return jsonObject;
    }

}
