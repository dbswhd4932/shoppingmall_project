package com.project.shop.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ControllerSetting {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

}
