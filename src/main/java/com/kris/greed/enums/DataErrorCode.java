package com.kris.greed.enums;

/**
 * @author Kris
 * @date 2019/4/15
 */
@SuppressWarnings("all")
public enum DataErrorCode {

    SUCCESS("00", "成功"),
    FAIL("20000001", "失败"),
    PARAM_ERROR("20000002", "参数错误"),
    NO_CONFIGURED_SERVICE("20000003", "没有匹配的服务");

    private String code;

    private String errorMsg;

    private DataErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
