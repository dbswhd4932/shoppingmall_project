package com.project.shop.goods.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import com.project.shop.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

public class ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ReviewServiceImpl reviewService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    ReviewRepository reviewRepository;


}
