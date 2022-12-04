package com.project.shop.order.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentApiController {

    private IamportClient api;

    @Value("${imp_key}")
    private String api_key;
    @Value("${imp_secret}")
    private String api_secretKey;

    public PaymentApiController() {
        this.api = new IamportClient("3264831242187573", "Qcc7UPisFbLFbUpihBKGGY6zsqJXGt1FoROR7BXGoSxh9jvQs6OHGTbJzZQ1yjiqPdEMx7zRKIBahfsN");
    }

    // 토큰받기
    @GetMapping("/getToken")
    public IamportResponse<AccessToken> getAuth() throws IamportResponseException, IOException {
        return api.getAuth();
    }

    // imp_uid 로 결제 내용 조회
    @GetMapping("/payment/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
        return api.paymentByImpUid(imp_uid);
    }
}
