package com.syliu.miaosha.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
//    @KafkaListener(topics = {"topic2"})
//    public  void onMessage1(ConsumerRecord<?,?>record){
//        System.out.println("messgae"+record.topic()+record.partition()+record.value());
//
//    }
}
