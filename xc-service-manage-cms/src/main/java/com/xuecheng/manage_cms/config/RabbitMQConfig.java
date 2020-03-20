package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public  static  final String EXCHANGE_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    @Bean(value = EXCHANGE_ROUTING_CMS_POSTPAGE)
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(EXCHANGE_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
}
