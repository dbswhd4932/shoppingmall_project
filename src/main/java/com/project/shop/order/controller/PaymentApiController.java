package com.project.shop.order.controller;

import com.project.shop.order.controller.request.AuthData;
import com.project.shop.order.service.impl.Iamportservice;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.ProtocolException;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentApiController {

    private final Iamportservice iamportservice;
    private IamportClient api;

    public PaymentApiController(Iamportservice iamportservice) {
        this.iamportservice = iamportservice;
        this.api = new IamportClient("3264831242187573", "Qcc7UPisFbLFbUpihBKGGY6zsqJXGt1FoROR7BXGoSxh9jvQs6OHGTbJzZQ1yjiqPdEMx7zRKIBahfsN");
    }

    @PostMapping("/user/getToken")
    public void getToken(@RequestBody AuthData authData) throws JSONException, ProtocolException {
        iamportservice.getToken(authData);
    }

    // imp_uid 로 결제 내용 조회
    @GetMapping("/payment/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
        return api.paymentByImpUid(imp_uid);
    }





}
