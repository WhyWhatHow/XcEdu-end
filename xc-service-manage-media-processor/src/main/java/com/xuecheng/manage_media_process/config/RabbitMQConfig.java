package com.xuecheng.manage_media_process.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:04
 **/
@Configuration
public class RabbitMQConfig {

    public static final String EX_MEDIA_PROCESSTASK = "ex_media_processor";

    //视频处理队列
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    public String queue_media_video_processTask;

    //视频处理路由
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public String routingkey_media_video;


    /**
     * 交换机配置
     *
     * @return the exchange
     */
    @Bean(EX_MEDIA_PROCESSTASK)
    public Exchange EX_MEDIA_VIDEOTASK() {
        return ExchangeBuilder.directExchange(EX_MEDIA_PROCESSTASK).durable(true).build();
    }

    //声明队列
    @Bean("queue_media_video_processTask")
    public Queue QUEUE_PROCESSTASK() {
        Queue queue = new Queue(queue_media_video_processTask, true, false, true);
        return queue;
    }

    /**
     * 绑定队列到交换机 .
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding binding_queue_media_processTask(@Qualifier("queue_media_video_processTask") Queue queue,
                                                   @Qualifier(EX_MEDIA_PROCESSTASK) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingkey_media_video).noargs();
    }


    // 配置容器工厂,将rabbitMQ从单线程转变为多线程

    //消费者并发数量
    public static final int DEFAULT_CONCURRENT = 10;

    @Bean("consumerContainerFactory")
    public SimpleRabbitListenerContainerFactory consumerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                         ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(DEFAULT_CONCURRENT);// 设置并发数量
        factory.setMaxConcurrentConsumers(DEFAULT_CONCURRENT);
        configurer.configure(factory, connectionFactory);
        return factory;
    }


}



