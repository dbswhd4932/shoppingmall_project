package com.project.shop.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse of(String errorCode , String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(errorCode.getErrorCode())
                        .errorMessage(errorCode.getMessage())
                        .build());

    }
}
