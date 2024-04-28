package com.example.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class KafkaProducer {
    @Resource
    private KafkaTemplate<String,Object> kafkaTemplate;

    @GetMapping("/kafka/send/{message}")
    public void sendMessage(@PathVariable String message) {
        kafkaTemplate.send("foo",null,message);
    }
}
