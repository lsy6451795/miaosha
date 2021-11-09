package com.syliu.miaosha.kafka;

import com.syliu.miaosha.rabbitmq.MQSender;
import com.syliu.miaosha.rabbitmq.MiaoshaMessage;
import com.syliu.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MQProducer {
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;
    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    public void sendMiaoshaMessage(MiaoshaMessage miaoshaMessage) {
        String msg = RedisService.beanToString(miaoshaMessage);
        log.info("send message:"+msg);
        kafkaTemplate.send("topic1",msg);
    }
}
