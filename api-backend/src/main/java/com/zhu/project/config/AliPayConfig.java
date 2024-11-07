//package com.zhu.project.config;
//
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AliPayConfig {
//
//    @Value("${alipay.app-id}")
//    private String appId;
//
//    @Value("${alipay.private-key}")
//    private String privateKey;
//
//    @Value("${alipay.alipay-public-key}")
//    private String alipayPublicKey;
//
//    @Value("${alipay.server-url}")
//    private String serverUrl;
//
//    @Value("${alipay.charset}")
//    private String charset;
//
//    @Value("${alipay.sign-type}")
//    private String signType;
//
//    @Bean
//    public AlipayClient alipayClient(){
//        return new DefaultAlipayClient(serverUrl, appId, privateKey, "json", charset, alipayPublicKey, signType);
//    }
//
//}
