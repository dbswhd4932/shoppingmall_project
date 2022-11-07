package com.project.shop.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR , "001" , "테스트 에러"),

    // 회원
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "M-001", "존재하지 않는 회원입니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "M-002", "이미 존재하는 LoginId 입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "M-003", "이미 존재하는 Email 입니다."),

    // 상품
    NOT_FOUND_GOODS(HttpStatus.BAD_REQUEST, "G-001" , "존재하지 않는 상품입니다.")


    ;

    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String errorCode;
    private String message;

}
