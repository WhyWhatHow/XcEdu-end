package com.xuecheng.test.freemarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication

@ComponentScan("com.xuecheng")
public class FreeMarkerAppliaction {
    public static void main(String[] args) {
        SpringApplication.run(FreeMarkerAppliaction.class,args);
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory()); // 使用restTemplate 处理请求
    }
}
