package com.project.shop.member.controller;

import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateDto;
import com.project.shop.member.domain.response.CardResponseDto;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CardService;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;
    private final MemberRepository memberRepository;

    @PostMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    public CardResponseDto create(@Valid @RequestBody CardCreateDto cardCreateDto, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("찾는 회원이 없습니다."));
        cardService.create(cardCreateDto, member);
        return new CardResponseDto(member, cardCreateDto.getCardCompany(), cardCreateDto.getCardNumber(),
                cardCreateDto.getCardExpire());
    }

    @GetMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    public List<CardResponseDto> findAll() {
        return cardService.findAll();
    }
}
