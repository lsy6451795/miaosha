package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.CodeMsg;
import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.domain.OrderInfo;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.GoodsService;
import com.syliu.miaosha.service.OrderService;
import com.syliu.miaosha.service.UserService;
import com.syliu.miaosha.vo.GoodsVo;
import com.syliu.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;
    @RequestMapping("/detail")
    @ResponseBody
   // @NeedLogin
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user, @RequestParam("orderId")long orderId){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo==null)
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        long goodsId=orderInfo.getGoodsId();
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo=new OrderDetailVo();
        orderDetailVo.setOrder(orderInfo);
        orderDetailVo.setGoods(goods);
        return Result.success(orderDetailVo);
    }


}
