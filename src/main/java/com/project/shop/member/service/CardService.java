package com.project.shop.member.service;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateDto;
import com.project.shop.member.domain.response.CardResponseDto;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    /**
     * 카드생성
     */
    public void create(@RequestBody CardCreateDto cardCreateDto, Member member) {
        // 카드 등록할 회원 찾기
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        // 카드 생성
        Card card = Card.builder()
                .member(findMember)
                .cardCompany(cardCreateDto.getCardCompany())
                .cardNumber(cardCreateDto.getCardNumber())
                .cardExpire(cardCreateDto.getCardExpire())
                .build();
        // 카드 저장
        cardRepository.save(card);
    }

    /**
     * 카드전체 조회
     */
    @Transactional(readOnly = true)
    public List<CardResponseDto> findAll() {
        return cardRepository.findAll().stream()
                .map(CardResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 카드 단건 조회
     */
    @Transactional(readOnly = true)
    public CardResponseDto findOne(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CARD));
        CardResponseDto cardResponseDto = new CardResponseDto(card);
        return cardResponseDto;
    }


    /**
     * 카드삭제
     */
    public void delete(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 카드입니다."));
        cardRepository.delete(card);
    }
}
