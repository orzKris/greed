package com.kris.greed.exception;

public class ExcelGenerateException extends RuntimeException {

    public ExcelGenerateException() {
        super("Excel生成失败！");
    }
}
