package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import com.project.shop.goods.controller.response.CategoryResponse;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.service.Impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ImportAutoConfiguration(WebSecurityConfig.class)
public class CategoryControllerTest extends ControllerSetting{

    @MockBean
    CategoryServiceImpl categoryService;

    @MockBean
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 생성")
    @WithMockUser(roles= "ADMIN")
    void categoryCreateTest() throws Exception {
        //given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest
                .builder().category("의류").build();

        //when
        mockMvc.perform(post("/api/categories")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryCreateRequest)))
                .andExpect(status().isCreated());

        //then
        verify(categoryService).categoryCreate(refEq(categoryCreateRequest));
    }

    @Test
    @DisplayName("카테고리 전체조회")
    void categoryFindAllTest() throws Exception {
        //given
        CategoryResponse categoryResponse = CategoryResponse.builder().category("모자").build();
        given(categoryService.categoryFindAll()).willReturn(List.of(categoryResponse));

        //when then
        mockMvc.perform(get("/api/categories")
                .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].category").value("모자"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 수정")
    @WithMockUser(roles= "ADMIN")
    void categoryEdit() throws Exception {
        //given
        CategoryEditRequest categoryEditRequest = CategoryEditRequest.builder().category("가방").build();

        //when
        mockMvc.perform(put("/api/categories/{categoryId}", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryEditRequest)))
                .andExpect(status().isOk());

        //then
        verify(categoryService).categoryEdit(anyLong(), refEq(categoryEditRequest));



    }

    @Test
    @DisplayName("카테고리 삭제")
    @WithMockUser(roles= "ADMIN")
    void categoryDeleteTest() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/categories/{categoryId}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        verify(categoryService).categoryDelete(1L);

    }

}
