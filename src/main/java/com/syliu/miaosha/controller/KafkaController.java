package com.syliu.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {
    @Autowired
    private KafkaTemplate<String,Object>kafkaTemplate;
    @RequestMapping("/kafka/{message}")
    public void sendMessage(@PathVariable("message")String message){
        kafkaTemplate.send("topic1",message);
    }
}
