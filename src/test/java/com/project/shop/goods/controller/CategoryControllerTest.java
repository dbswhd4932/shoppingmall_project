package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("컨트롤러 카테고리 통합테스트")
public class CategoryControllerTest extends ControllerSetting {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 생성")
    void createCategory() throws Exception {
        //given
        CategoryCreateRequest categoryCreateRequest =
                CategoryCreateRequest.builder().category("벨트").build();

        //when
        mockMvc.perform(post("/api/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateRequest)))
                .andExpect(status().isCreated());

        //then
        assertThat(categoryRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    //todo 전체 테스트 시 401 에러 발생.
    @DisplayName("카테고리 조회")
    void categoryFindAll() throws Exception {
        //given
        Category category = Category.builder().category("테스트").build();
        categoryRepository.save(category);
        //when
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
        //then
        assertThat(categoryRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 수정")
    void categoryEdit() throws Exception {
        //given
        Category category = Category.builder().category("테스트").build();
        categoryRepository.save(category);
        CategoryEditRequest categoryEditRequest =
                CategoryEditRequest.builder().category("모자").build();

        //when
        mockMvc.perform(put("/api/categories/{categoryId}", category.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryEditRequest)))
                .andExpect(status().isOk());

        //then
        assertThat(categoryRepository.findById(category.getId()).get().getCategory()).isEqualTo("모자");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 삭제")
    void categoryDelete() throws Exception {
        //given
        Category category = Category.builder().category("의류").build();
        categoryRepository.save(category);
        //when
        mockMvc.perform(delete("/api/categories/{categoryId}", category.getId()))
                .andExpect(status().isNoContent());
        //then
        assertThat(categoryRepository.findAll().size()).isEqualTo(0);
    }
}
