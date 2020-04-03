package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient//允许eureka发现该服务
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")// 扫描实体类
@ComponentScan("com.xuecheng.api")// 扫描接口
@ComponentScan("com.xuecheng.manage_cms") // 扫描本项目
@ComponentScan("com.xuecheng.framework.exception")// 同意异常处理
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
