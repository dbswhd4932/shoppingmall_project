package com.project.shop.member.service;

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
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("찾는 회원이 없습니다"));
        Card card = Card.builder()
                .member(findMember)
                .cardCompany(cardCreateDto.getCardCompany())
                .cardNumber(cardCreateDto.getCardNumber())
                .cardExpire(cardCreateDto.getCardExpire())
                .build();
        cardRepository.save(card);
    }

    /**
     * 카드전체 조회
     */
    @Transactional(readOnly = true)
    public List<CardResponseDto> findAll() {
        return cardRepository.findAll().stream()
                .map(card -> new CardResponseDto(card)).collect(Collectors.toList());
    }

    /**
     * 카드 단건 조회
     */
    @Transactional(readOnly = true)
    public CardResponseDto findOne(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 카드입니다."));
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
