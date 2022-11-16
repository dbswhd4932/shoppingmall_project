package com.project.shop.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR , "001" , "테스트 에러"),

    // 회원
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "Member-001", "존재하지 않는 회원입니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "Member-002", "이미 존재하는 아이디 입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "Member-003", "이미 존재하는 이메일 입니다."),

    // 권한
    NOT_FOUND_AUTHORITY(HttpStatus.BAD_REQUEST, "Role-001", "존재하지 않는 권한입니다."),

    // 상품
    NOT_FOUND_GOODS(HttpStatus.BAD_REQUEST, "Goods-001" , "존재하지 않는 상품입니다."),
    DUPLICATE_GOODS(HttpStatus.BAD_REQUEST, "Goods-002" , "이미 존재하는 상품입니다."),
    NOT_BUY_GOODS(HttpStatus.BAD_REQUEST, "Goods-003" , "구매한 상품이 아닙니다."),

    // 카드
    NOT_FOUND_CARD(HttpStatus.BAD_REQUEST, "Card-001" , "존재하지 않는 카드입니다."),

    // 장바구니
    NOT_FOUND_CART(HttpStatus.BAD_REQUEST, "Cart-001", "존재하지 않는 장바구니 입니다."),

    // 카테고리
    NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "Category-001", "존재하지 않는 카테고리입니다"),

    // 리뷰
    NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "Review-001", "존재하는 리뷰가 없습니다."),
    NOT_MATCH_REVIEW(HttpStatus.BAD_REQUEST, "Review-002" , "해당 회원이 작성한 리뷰가 아닙니다."),
    CANT_DELETE_REVIEW(HttpStatus.BAD_REQUEST, "Review-003", "삭제할 수 없는 리뷰입니다."),

    // 주문
    NOT_FOUND_ORDERS(HttpStatus.BAD_REQUEST, "Order-001", "존재하는 주문이 없습니다."),

    // 결제
    NOT_FOUND_PAY(HttpStatus.BAD_REQUEST, "Pay-001", "존재하는 결제가 없습니다.")
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
