package com.project.shop.member.service;

import com.project.shop.member.domain.request.CardCreateRequest;
import com.project.shop.member.domain.response.CardResponse;

import java.util.List;

public interface CardService {

    //생성
    void cardCreate(CardCreateRequest cardCreateRequest);

    //전체조회
    List<CardResponse> cardFindAll();

    //카드 1개조회 - 따로 사용 X
    //카드 수정 - 따로 사용 X

    //회원 별 카드 조회
    List<CardResponse> cardFindByMemberId(Long memberId);

    //삭제
    void cardDelete(Long cardId);


}
