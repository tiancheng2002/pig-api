package com.zhu.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ErrorResponse businessExceptionHandler(ApiException e) {
        log.error("BusinessException", e);
        return new ErrorResponse(e.getCode(),e.getMessage());
    }

}
