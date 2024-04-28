package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = {"foo"})
    public void listen(ConsumerRecord<?, ?> record) {
        System.out.println("收到服务器的应答: " + record.value().toString());
    }
}
