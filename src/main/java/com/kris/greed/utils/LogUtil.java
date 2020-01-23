package com.kris.greed.utils;

import lombok.extern.log4j.Log4j2;

/**
 * 日志规范util
 *
 * @author by Kris on 2018/12/7.
 */
@Log4j2
public class LogUtil {

    public static void logInfo(String conditionMessage) {
        log.info("[TRACE_INFO]: {}", conditionMessage);
    }

    public static void logWarn(String requestTime, String conditionMessage, String errorMessage) {
        log.error("[TRACE_WARN]: request_time={},{},{} ", requestTime, conditionMessage, errorMessage);
    }

    public static void logError(String requestTime, String conditionMessage, String errorMessage, Exception e) {
        log.error("[TRACE_ERROR]: request_time={},{},{},{} ", requestTime, conditionMessage, errorMessage, e);
    }

}
