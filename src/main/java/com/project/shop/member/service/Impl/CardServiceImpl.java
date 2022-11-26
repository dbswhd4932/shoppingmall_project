package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.Card;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.CardCreateRequest;
import com.project.shop.member.controller.response.CardResponse;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    //카드 생성
    @Override
    public void cardCreate(CardCreateRequest cardCreateRequest) {
        Member member = memberRepository.findById(cardCreateRequest.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        Card card = Card.createCard(member, cardCreateRequest);
        cardRepository.save(card);
    }

    //카드 전제 조회
    @Transactional(readOnly = true)
    @Override
    public List<CardResponse> cardFindAll() {
        return cardRepository.findAll()
                .stream().map(CardResponse::new).collect(Collectors.toList());
    }

    // 카드 회원 별 조회
    @Transactional(readOnly = true)
    @Override
    public List<CardResponse> cardFindMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        List<Card> cardList = cardRepository.findByMemberId(member.getId());
        return cardList.stream().map(CardResponse::new).collect(Collectors.toList());
    }

    // 카드 삭제
    @Override
    public void cardDelete(Long cardId, Long memberId) {
        Card card = cardRepository.findByIdAndMemberId(cardId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CARD));

        cardRepository.delete(card);
    }
}
