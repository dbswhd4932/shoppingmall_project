package com.project.shop.order.service.impl;

import com.project.shop.order.controller.request.AuthData;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

//todo 필요없는것 같음.
@Service
public class Iamportservice {

    public void savePayData() {

    }

    public void getToken(AuthData authData) throws JSONException, ProtocolException {
        // JSON 데이터만들기
        System.out.println("getToken");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imp_key", authData.getApi_key());
        jsonObject.put("imp_secret", authData.getApi_secret());

        try {
            String host_url = "https://api.iamport.kr/users/getToken";
            HttpURLConnection conn = null;

            URL url = new URL(host_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            //POST 방식으로 스트링을 통한 JSON 전송
            conn.setDoOutput(true);
            BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(conn.getOutputStream())));

            bw.write(jsonObject.toString());
            bw.flush();
            bw.close();

            // 서버에서 보낸 응답 데이터 수신받기
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String returnMsg = in.readLine();
            System.out.println("응답메시지 : " + returnMsg);

            //HTTP 응답 코드 수신
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                System.out.println("400:명령을 실행 오류");
            } else if (responseCode == 500) {
                System.out.println("500 : 서버 에러");
            } else {
                System.out.println(responseCode + " 정상 응답 ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}



