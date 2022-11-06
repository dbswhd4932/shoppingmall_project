package com.project.shop.global.error.exception;

import com.project.shop.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getErrorCode());
        this.errorCode = errorCode;
    }
}
