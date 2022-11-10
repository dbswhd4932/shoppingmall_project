package com.project.shop.member.service;

import com.project.shop.factory.CardFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateDto;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    RoleService cardService;

    @Mock
    CardRepository cardRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("카드생성 테스트")
    void createTest() {
        Member member = MemberFactory.createMember();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        CardCreateDto cardCreateDto = CardFactory.cardCreateDto();

        cardService.create(cardCreateDto , member);

        Card card = Card.builder()
                .member(member)
                .cardCompany(cardCreateDto.getCardCompany())
                .cardNumber(cardCreateDto.getCardNumber())
                .cardExpire(cardCreateDto.getCardExpire())
                .build();

        given(cardRepository.save(card)).willReturn(card);
        Card savedCard = cardRepository.save(card);
        System.out.println("savedCard = " + savedCard);

        assertThat(savedCard.getCardCompany()).isEqualTo("국민은행");
        assertThat(savedCard.getCardNumber()).isEqualTo("1234");
        assertThat(savedCard.getCardExpire()).isEqualTo("11-11");
        assertThat(savedCard.getMember().getLoginId()).isEqualTo(member.getLoginId());

    }

    @Test
    @DisplayName("카드 전체조회 테스트")
    void findAllTest() {

        Card card1 = CardFactory.createCard();
        Card card2 = CardFactory.createCard();

        cardRepository.save(card1);
        cardRepository.save(card2);
        given(cardRepository.findAll()).willReturn(List.of(card1, card2));

        assertThat(cardService.findAll().size()).isEqualTo(2);

    }


}