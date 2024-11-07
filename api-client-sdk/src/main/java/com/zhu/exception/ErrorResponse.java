package com.zhu.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorMessage;
    private int code;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorMessage = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    public ErrorResponse(int code, String errorMessage) {
        this.errorMessage = errorMessage;
        this.code = code;
    }
}