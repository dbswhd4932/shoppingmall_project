package com.project.shop.member.service;

import com.project.shop.member.controller.request.CardCreateRequest;
import com.project.shop.member.controller.response.CardResponse;

import java.util.List;

public interface CardService {

    //생성
    void cardCreate(CardCreateRequest cardCreateRequest);

    //회원 별 카드 조회
    List<CardResponse> cardFindMember(Long memberId);

    //전체조회
    List<CardResponse> cardFindAll();

    //삭제
    void cardDelete(Long cardId , Long memberId);

}
