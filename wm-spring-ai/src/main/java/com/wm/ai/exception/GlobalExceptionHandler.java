package com.wm.ai.exception;

import com.alibaba.fastjson2.JSON;
import com.wm.ai.common.WebResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public WebResponse handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("GlobalExceptionHandler, requestURI: [{}], [业务异常] , code:{}, message:{}",
            request.getRequestURI(), errorCode.getCode(), errorCode.getMessage(), e);
        response.setStatus(HttpStatus.OK.value());
        WebResponse webResponse = new WebResponse();
        WebResponse.Result result = new WebResponse.Result();
        result.setCode(errorCode.getCode());
        result.setMessage(e.getMessage());
        webResponse.setResult(result);
        return webResponse;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public WebResponse globalException(HttpServletRequest request,HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.warn("GlobalExceptionHandler, requestURI: [{}], [不支持的请求方式], current http method:{}",
            request.getRequestURI(), request.getMethod(), e);
        return WebResponse.failBusinessException(new BusinessException(ErrorCode.HTTP_METHOD_NOT_SUPPORT));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResponse globalException(HttpServletRequest request,HttpServletResponse response, MethodArgumentNotValidException e) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.warn("GlobalExceptionHandler, requestURI: [{}], [请求参数校验失败]", request.getRequestURI(), e);

        WebResponse webResponse = new WebResponse();
        webResponse.setResult(ErrorCode.VALIDATION_FAIL);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        webResponse.setData(allErrors);
        return webResponse;
    }


    @ExceptionHandler(Exception.class)
    public WebResponse internalServerErrorException(HttpServletRequest request,HttpServletResponse response, Exception e) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.error("GlobalExceptionHandler[系统出现500异常][服务器内部错误], requestURI: [{}], headers: [{}],",
            request.getRequestURI(), JSON.toJSONString(getHeaders(request)), e);
        return WebResponse.failInternalException(e);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }
}
