package com.project.shop.global.error;

import com.project.shop.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(BusinessException e) {
        log.error("BusinessException" , e);
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }
}
