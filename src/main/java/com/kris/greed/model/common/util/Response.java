package com.kris.greed.model.common.util;

import com.kris.greed.model.common.exception.ErrorCode;
import org.slf4j.MDC;
import java.text.MessageFormat;

public class Response<T> {

    private String responseCode;

    private T result;

    private String message = "成功";

    private String traceId;

    public Response() {
    }

    public Response(String responseCode, String message, T result) {
        this.responseCode = responseCode;
        this.message = message;
        this.result = result;
        this.traceId = MDC.get("X-B3-TraceId");
    }

    public static Response message(String message) {
        return new Response("00", message, (Object) null);
    }

    public static <T> Response<T> ok(T result) {
        return new Response("00", "成功", result);
    }

    public static Response error(ErrorCode errorCode, Object... params) {
        String message = MessageFormat.format(errorCode.getErrorMsg(), params);
        return new Response(errorCode.getCode(), message, (Object) null);
    }

    public static Response notFormatError(ErrorCode errorCode, String params) {
        return new Response(errorCode.getCode(), params, (Object) null);
    }

    public static Response error(ErrorCode code) {
        return error(code, code.getErrorMsg());
    }

    public static Response message() {
        return message("成功");
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public T getResult() {
        return this.result;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Response)) {
            return false;
        } else {
            Response<?> other = (Response) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59:
                {
                    Object this$responseCode = this.getResponseCode();
                    Object other$responseCode = other.getResponseCode();
                    if (this$responseCode == null) {
                        if (other$responseCode == null) {
                            break label59;
                        }
                    } else if (this$responseCode.equals(other$responseCode)) {
                        break label59;
                    }

                    return false;
                }

                Object this$result = this.getResult();
                Object other$result = other.getResult();
                if (this$result == null) {
                    if (other$result != null) {
                        return false;
                    }
                } else if (!this$result.equals(other$result)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$traceId = this.getTraceId();
                Object other$traceId = other.getTraceId();
                if (this$traceId == null) {
                    if (other$traceId != null) {
                        return false;
                    }
                } else if (!this$traceId.equals(other$traceId)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Response;
    }

    @Override
    public String toString() {
        return "Response(responseCode=" + this.getResponseCode() + ", result=" + this.getResult() + ", message=" + this.getMessage() + ", traceId=" + this.getTraceId() + ")";
    }
}
