package com.example.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RabbitConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "an.queue")
    @RabbitHandler
    public void receiveMessage(@Payload String msg,
                               Channel channel,
                               Message message) throws Exception {
        System.out.println("Received message from queue: " + msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(queues = "queue_dlx")
    @RabbitHandler
    public void receiveMessage1(@Payload String msg,
                               Channel channel,
                               Message message) throws Exception {
        System.out.println("Received message from queue: " + msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
