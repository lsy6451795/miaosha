package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.User;
import com.syliu.miaosha.rabbitmq.MQSender;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.redis.UserKey;
import com.syliu.miaosha.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    MQSender mqSender;
    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("hello sy");
        return  Result.success("hello");
    }
    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic(){
        mqSender.sendTopic("hello sy t");
        return  Result.success("hello");
    }
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout(){
        mqSender.sendFanout("hello sy t");
        return  Result.success("hello");
    }
    @RequestMapping("/thymeleaf")
   String thymeleaf(Model model){
        model.addAttribute("name","syliu");
        return  "hello";
    }
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){

        User user = userService.getById(2);

        return  Result.success(user);
    }
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){

       userService.tx();

        return  Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){

        User user =redisService.get(UserKey.getById,""+1, User.class);

        return  Result.success(user);
    }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user=new User();
        user.setId(1);
        user.setName("111");
        boolean v1 =redisService.set(UserKey.getById,""+1, user);
        return  Result.success(v1);
    }

}
