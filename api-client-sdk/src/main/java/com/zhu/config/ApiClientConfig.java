package com.zhu.config;

import com.zhu.client.ApiClient;
import com.zhu.service.ApiService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("zhuapi.client")
@Data
@ComponentScan
public class ApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public ApiClient apiClient(){
        return new ApiClient(accessKey,secretKey);
    }

    @Bean
    public ApiService apiService(){
        ApiService apiService = new ApiService();
        apiService.setApiClient(new ApiClient(accessKey, secretKey));
        return apiService;
    }

}
