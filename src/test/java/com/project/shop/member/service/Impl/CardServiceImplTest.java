package com.project.shop.member.service.Impl;

import com.project.shop.factory.CardFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.Card;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.CardCreateRequest;
import com.project.shop.member.controller.response.CardResponse;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @InjectMocks
    CardServiceImpl cardService;

    @Mock
    CardRepository cardRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("카드 생성")
    void cardCreateTest() {
        //given
        Member member = MemberFactory.createMember();
        CardCreateRequest cardCreateRequest = CardCreateRequest.builder()
                .memberId(member.getId())
                .cardCompany("국민")
                .cardNumber("1234-1234-1234-1234")
                .cardExpire("23-12")
                .build();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        cardService.cardCreate(cardCreateRequest);

        //then
        verify(cardRepository).save(any());

    }

    @Test
    @DisplayName("카드 전체 조회")
    void cardFindAllTest() {
        //given
        Member member = MemberFactory.createMember();
        Card card1 = CardFactory.cardCreate(member);
        Card card2 = CardFactory.cardCreate(member);
        given(cardRepository.findAll()).willReturn(List.of(card1, card2));
        //when
        List<CardResponse> cardResponses = cardService.cardFindAll();

        //then
        assertThat(cardResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("카드 회원 별 조회")
    void cardFindByMemberIdTest() {
        //given
        Member member = MemberFactory.createMember();
        Card card1 = CardFactory.cardCreate(member);
        Card card2 = CardFactory.cardCreate(member);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(cardRepository.findByMemberId(member.getId())).willReturn(List.of(card1, card2));

        //when
        List<CardResponse> cardResponses = cardService.cardFindMember(member.getId());

        //then
        assertThat(cardResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("카드 삭제")
    void cardDeleteTest() {
        //given
        Member member = MemberFactory.createMember();
        Card card = CardFactory.cardCreate(member);
        given(cardRepository.findByIdAndMemberId(card.getId(), member.getId()))
                .willReturn(Optional.of(card));

        //when
        cardService.cardDelete(card.getId(), member.getId());

        //then
        verify(cardRepository).delete(card);
    }


}
