package com.wm.ai.exception;

import java.util.HashMap;
import java.util.Map;

public class BusinessException extends RuntimeException{
    private ErrorCode errorCode;

    private Map<String, String> params;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void addParam(String name, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }

        this.params.put(name, value);
    }

}
