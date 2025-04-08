package com.wm.ai.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wm.ai.exception.BusinessException;
import com.wm.ai.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

@RestControllerAdvice(basePackages = {"com.wm.ai.controller"})
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private  ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        return !(returnType.getParameterType().equals(WebResponse.class));
    }

    @Override
    public Object beforeBodyWrite(Object data,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        Method method = returnType.getMethod();
        if (method != null && method.getReturnType() != null) {
            if (returnType.getGenericParameterType().equals(String.class)) {
                try {
                    return objectMapper.writeValueAsString(WebResponse.returnSuccessData(data));
                } catch (JsonProcessingException e) {
                    throw new BusinessException("ResponseController返回类型错误", ErrorCode.FAIL);
                }
            } else if (method.getReturnType().equals(Void.TYPE)) {
                return WebResponse.returnSuccess();
            }
        }
        return WebResponse.returnSuccessData(data);
    }
}
