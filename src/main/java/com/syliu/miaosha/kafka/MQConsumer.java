package com.syliu.miaosha.kafka;

import com.syliu.miaosha.domain.MiaoshaOrder;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.rabbitmq.MiaoshaMessage;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.GoodsService;
import com.syliu.miaosha.service.MiaoshaService;
import com.syliu.miaosha.service.OrderService;
import com.syliu.miaosha.vo.GoodsVo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MQConsumer {
    private  static Logger log= LoggerFactory.getLogger(MQConsumer.class);
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @KafkaListener(topics = {"topic1"})
    public void recieve(ConsumerRecord<?,?> record){
        String message= (String) record.value();
        log.info("receive message:"+message);
        MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }
}
