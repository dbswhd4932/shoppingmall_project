package com.project.shop.order.controller;

import com.project.shop.order.controller.request.AuthData;
import com.project.shop.order.service.impl.Iamportservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.net.ProtocolException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentApiController {

    private final Iamportservice iamportservice;

    @PostMapping(value = "/user/getToken")
    public void getToken(@RequestBody AuthData authData) throws JSONException, ProtocolException {
        iamportservice.getToken(authData);
    }

}
