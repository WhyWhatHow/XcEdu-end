package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;




@Component
public class ReceiverHandler {
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public  void dealWithEmail(String msg, Message message, Channel channel){
        System.out.println(msg);
        System.out.println(message.toString());
        System.out.println(channel.toString());
    }
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
    public  void dealWithSms(String msg, Message message, Channel channel){
        System.out.println(msg);
        System.out.println(message.toString());
        System.out.println(channel.toString());
    }

}
