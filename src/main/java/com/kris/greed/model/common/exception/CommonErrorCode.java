package com.kris.greed.model.common.exception;

public enum  CommonErrorCode implements ErrorCode {

    SYSTEM_ERROR("10000001","服务处理异常，请联系客服"),

    TOO_MANY_REQUEST("10000429","请求过于频繁"),

    BAD_REQUEST("10000400","无效的请求：{0}"),

    UNAUTHORIZED("10000401","未授权"),

    NO_RESOURCE("10000404","找不到请求资源"),

    MEDIATYPE_NOT_SUPPORT("10000415","Content-Type不支持：{0}"),

    METHOD_NOT_ALLOWED("10000405","请求方法不支持：{0}")
    ;

    private String code;

    private String errorMsg;

    private CommonErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
