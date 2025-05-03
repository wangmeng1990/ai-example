package com.wm.ai.common;

import com.wm.ai.exception.BusinessException;
import com.wm.ai.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class WebResponse<T> {

    private Result result = new Result();

    @Schema(description = "返回数据内容")
    private T data;


    public void setResult(Result result) {
        this.result = result;
    }

    public void setResult(ErrorCode errorCode) {
        this.result.setCode(errorCode.getCode());
        this.result.setMessage(errorCode.getMessage());
    }

    public Result getResult() {
        return result;
    }

    public static WebResponse<?> returnSuccess() {
        WebResponse<?> webResponse = new WebResponse<>();
        webResponse.setResult(ErrorCode.SUCCESS);
        return webResponse;
    }
    public static WebResponse<?> returnFail() {
        WebResponse<?> webResponse = new WebResponse<>();
        webResponse.setResult(ErrorCode.FAIL);
        return webResponse;
    }


    public static <T> WebResponse<T> returnSuccessData(T data) {
        WebResponse<T> webResponse = new WebResponse<>();
        webResponse.setData(data);
        webResponse.setResult(ErrorCode.SUCCESS);
        return webResponse;
    }

    public static WebResponse failBusinessException(BusinessException e) {
        WebResponse webResponse = new WebResponse();
        webResponse.getResult().setCode(e.getErrorCode().getCode());
        webResponse.getResult().setMessage(e.getMessage());
        webResponse.getResult().setMessageParams(e.getParams());
        return webResponse;
    }

    public static WebResponse failInternalException(Exception e) {
        WebResponse webResponse = new WebResponse();
        webResponse.setResult(ErrorCode.INTERNAL_SERVER_ERROR_500);
        return webResponse;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Data
    public static class Result implements Serializable {
        @Schema(description = "返回代码")
        private Integer code;

        @Schema(description = "错误消息")
        private String message;

        private Map<String, String> messageParams;

        public Result(){
            this.code = 0;
        }

        public Map<String, String> getMessageParams() {
            return messageParams;
        }

        public void setMessageParams(Map<String, String> params) {
            this.messageParams = params;
        }
    }
}
