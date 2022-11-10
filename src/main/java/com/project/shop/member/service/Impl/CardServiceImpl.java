package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateRequest;
import com.project.shop.member.domain.response.CardResponse;
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
        Card card = Card.builder()
                .member(member)
                .cardCompany(cardCreateRequest.getCardCompany())
                .cardNumber(cardCreateRequest.getCardNumber())
                .cardExpire(cardCreateRequest.getCardExpire())
                .build();

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
    public List<CardResponse> cardFindByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        List<Card> cardList = cardRepository.findByMemberId(member.getId());
        return cardList.stream().map(CardResponse::new).collect(Collectors.toList());
    }

    // 카드 삭제
    @Override
    public void cardDelete(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));

        cardRepository.delete(card);
    }
}
