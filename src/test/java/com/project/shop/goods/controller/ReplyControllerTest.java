package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.service.Impl.ReplyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReplyController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("대댓글 컨트롤러 테스트")
class ReplyControllerTest extends ControllerSetting {

    @MockBean
    ReplyServiceImpl replyService;

    @Test
    @DisplayName("대댓글 생성")
    @WithMockUser(roles = "SELLER")
    void replyCreate() throws Exception {
        //given
        ReplyCreateRequest replyCreateRequest
                = ReplyCreateRequest.builder().reviewId(1L).replyComment("comment").build();

        //when
        mockMvc.perform(post("/api/replies")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyCreateRequest)))
                .andExpect(status().isCreated());

        //then
        verify(replyService).replyCreate(refEq(replyCreateRequest));
    }

    @Test
    @DisplayName("대댓글 조회")
    void replyFind() throws Exception {
        //given
        ReplyResponse replyResponse = ReplyResponse.builder().comment("comment").build();
        given(replyService.replyFind(1L)).willReturn(List.of(replyResponse));

        //when then
        mockMvc.perform(get("/api/reviews/reply")
                        .queryParam("reviewId", "1"))
                .andExpect(jsonPath("$.[0].comment").value("comment"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대댓글 수정")
    @WithMockUser(roles = "SELLER")
    void replyEdit() throws Exception {
        //given
        ReplyEditRequest replyEditRequest = ReplyEditRequest.builder().comment("editComment").build();

        //when
        mockMvc.perform(put("/api/replies/{replyId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyEditRequest)))
                .andExpect(status().isOk());

        //then
        verify(replyService).replyEdit(any(), refEq(replyEditRequest));
    }

    @Test
    @DisplayName("대댓글 삭제")
    @WithMockUser(roles = "SELLER , ADMIN")
    void replyDelete() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/replies/{replyId}", 1L))
                .andExpect(status().isNoContent());

        //then
        verify(replyService).replyDelete(1L);

    }

}