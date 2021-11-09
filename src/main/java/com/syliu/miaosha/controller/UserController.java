package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.GoodsService;
import com.syliu.miaosha.service.MiaoshaUserService;
import com.syliu.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {
    private static Logger logger= LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> to_list(Model model, MiaoshaUser user){

        return Result.success(user);
    }


}
