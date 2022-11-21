package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CardCreateRequest;
import com.project.shop.member.controller.response.CardResponse;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.member.service.Impl.CardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CardControllerTest extends ControllerSetting {

    @MockBean
    CardServiceImpl cardService;

    @MockBean
    CardRepository cardRepository;

    @Test
    @DisplayName("카드생성")
    void cardCreateTest() throws Exception {
        //given
        CardCreateRequest cardCreateRequest = CardCreateRequest.builder()
                .memberId(1L)
                .cardCompany("카드회사")
                .cardNumber("1234-1234-1234-1234")
                .cardExpire("23-12")
                .build();

        //when then
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated());

        // 주어진 값을 그대로 반영하는 객체 인자 -> refEq
        verify(cardService).cardCreate(refEq(cardCreateRequest));

    }

    @Test
    @DisplayName("카드 전체조회")
    void cardFindAllTest() throws Exception {
        //given
        CardResponse cardResponse = CardResponse.builder()
                .memberId(1L)
                .cardCompany("카드회사")
                .cardNumber("1234-1234-1234-1234")
                .cardExpire("23-12")
                .build();

        given(cardService.cardFindAll()).willReturn(List.of(cardResponse));

        //when then
        mockMvc.perform(get("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].cardCompany").value("카드회사"))
                .andExpect(jsonPath("$.[0].cardNumber").value("1234-1234-1234-1234"))
                .andExpect(jsonPath("$.[0].cardExpire").value("23-12"));
    }

    @Test
    @DisplayName("카드 회원 별 조회")
    void cardFindByMemberIdTest() throws Exception {
        //given
        CardResponse cardResponse = CardResponse.builder()
                .memberId(1L)
                .cardCompany("카드회사")
                .cardNumber("1234-1234-1234-1234")
                .cardExpire("23-12")
                .build();

        given(cardService.cardFindByMemberId(cardResponse.getMemberId())).willReturn(List.of(cardResponse));

        // when then
        mockMvc.perform(get("/api/cards/{memberId}", cardResponse.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].cardCompany").value("카드회사"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카드 삭제")
    void cardDeleteTest() throws Exception {
        // given
        Long cardId = 1L;

        // when then
        mockMvc.perform(delete("/api/cards/{memberId}", cardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cardService).cardDelete(cardId);
    }
}