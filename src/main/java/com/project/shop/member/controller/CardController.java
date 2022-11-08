package com.project.shop.member.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateDto;
import com.project.shop.member.domain.response.CardResponseDto;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CardService;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    /**
     *  카드 생성
     */
    @PostMapping("/card")
    public ResponseEntity<CardResponseDto> create(@Valid @RequestBody CardCreateDto cardCreateDto, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        cardService.create(cardCreateDto, member);
        CardResponseDto responseDto = CardResponseDto.builder()
                .memberId(member.getId())
                .cardCompany(cardCreateDto.getCardCompany())
                .cardNumber(cardCreateDto.getCardNumber())
                .cardExpire(cardCreateDto.getCardExpire())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    /**
     *  카드 전체 조회
     */
    @GetMapping("/card")
    public ResponseEntity<List<CardResponseDto>> findAll() {
        return ResponseEntity.ok(cardService.findAll());
    }

    /**
     *  카드 단건 조회
     */
    @GetMapping("/card/{id}")
    public ResponseEntity<CardResponseDto> findOne(@PathVariable("id") Long cardId) {
        CardResponseDto responseDto = cardService.findOne(cardId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     *  카드삭제
     */
    @DeleteMapping("/card/{id}")
    public void delete(@PathVariable("id") Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CARD));
        cardService.delete(cardId);
    }
}
