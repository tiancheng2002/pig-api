package com.zhu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    @GetMapping("/get")
    public Map getAddress(String ip) {
        OkHttpClient httpClient = new OkHttpClient();
        Map resultMap = null;
        String url = "http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String result = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            resultMap = objectMapper.readValue(result, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

}
