package com.wm.ai.exception;

import lombok.Data;

@Data
public class ErrorCode {

    public static final ErrorCode INTERNAL_SERVER_ERROR_500 = new ErrorCode(500, "服务器错误");

    public static final ErrorCode SUCCESS = new ErrorCode(0, "SUCCESS");

    public static final ErrorCode FAIL = new ErrorCode(1, "FAIL");

    public static final ErrorCode VALIDATION_FAIL = new ErrorCode(2, "VALIDATION FAIL");

    public static final ErrorCode HTTP_METHOD_NOT_SUPPORT = new ErrorCode(13, "方法没有找到, HTTP(GET, POST, DELETE ...) method not support");


    private int code;
    private String message;



    public ErrorCode() {
    }

    public ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}
