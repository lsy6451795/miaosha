package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.CodeMsg;
import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.MiaoshaUserService;
import com.syliu.miaosha.util.ValidatorUtil;
import com.syliu.miaosha.vo.LoginVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger= LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @RequestMapping("/to_login")
    public String to_login(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> do_login(HttpServletResponse response, @Valid LoginVo loginVo){
        logger.info(loginVo.toString());
       String token= miaoshaUserService.login(response,loginVo);
        return Result.success(token);
    }


}
