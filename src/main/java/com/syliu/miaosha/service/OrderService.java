package com.syliu.miaosha.service;

import com.syliu.miaosha.dao.OrderDao;
import com.syliu.miaosha.domain.MiaoshaOrder;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.domain.OrderInfo;
import com.syliu.miaosha.redis.OrderKey;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    RedisService redisService;
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long id, long goodsId) {
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+id+""+goodsId,MiaoshaOrder.class);
    }
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        System.out.println(miaoshaOrder.getOrderId());
        redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getId()+""+goodsVo.getId(),miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
