package com.project.shop.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ErrorResponse {

    private String errorMessage;

    @Builder
    public ErrorResponse( String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorResponse of( String errorMessage) {
        return ErrorResponse.builder()
                .errorMessage(errorMessage)
                .build();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorMessage(errorCode.getMessage())
                        .build());

    }
}
