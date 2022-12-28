package com.project.shop.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class ControllerSetting {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

}
