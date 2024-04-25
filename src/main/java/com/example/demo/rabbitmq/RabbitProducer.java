package com.example.demo.rabbitmq;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RabbitProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send/{message}")
    public void sendMessage(@PathVariable String message) {
        CorrelationData correlationData = new CorrelationData(message);
        String queueName = "dead.queue";
        String key = "";
        rabbitTemplate.convertAndSend(key, queueName, message, correlationData);
        System.out.println("Sent message to queue: " + message);
    }
}
