package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CardCreateRequest;
import com.project.shop.member.controller.response.CardResponse;
import com.project.shop.member.service.Impl.CardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {
    
    private final CardServiceImpl cardService;

    //카드 생성
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public void cardCreate(@RequestBody @Valid CardCreateRequest cardCreateRequest) {
        cardService.cardCreate(cardCreateRequest);
    }

    //카드 전체조회
    @GetMapping("/cards")
    @ResponseStatus(HttpStatus.OK)
    public List<CardResponse> cardFindAll() {
        return cardService.cardFindAll();
    }

    //카드 회원 별 조회
    @GetMapping("/cards/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CardResponse> cardFindByMemberId(@PathVariable("memberId") Long memberId) {
        return cardService.cardFindByMemberId(memberId);
    }

    //카드 삭제
    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public void cardDelete(@PathVariable("cardId") Long cardId) {
        cardService.cardDelete(cardId);
    }
}
