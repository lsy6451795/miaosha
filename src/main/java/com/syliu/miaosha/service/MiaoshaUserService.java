package com.syliu.miaosha.service;

import com.syliu.miaosha.Result.CodeMsg;
import com.syliu.miaosha.dao.MiaoshaUserDao;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.exception.GlobalException;
import com.syliu.miaosha.redis.KeyPrefix;
import com.syliu.miaosha.redis.MiaoshaUserKey;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.util.MD5Util;
import com.syliu.miaosha.util.UUIDUtil;
import com.syliu.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
    public static final String Cookie_Name_token="token";
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RedisService redisService;
    public MiaoshaUser getById(long id){
        MiaoshaUser user=redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user!=null){
            return user;
        }
        user=miaoshaUserDao.getById(id);
        if(user!=null)
            redisService.set(MiaoshaUserKey.getById,""+id,user);
        return user;
    }
    public boolean Update(String token,long id,String passwordNew){
        MiaoshaUser user=getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //减少更新字段数
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
//        //处理缓存
//        redisService.delete(MiaoshaUserKey.getById,""+id);
//        user.setPassword(passwordNew);
//        redisService.set(MiaoshaUserKey.token,""+id,user);
        return  true;
    }

    public String login(HttpServletResponse response,LoginVo loginVo) {

            if (loginVo == null) {
                throw new GlobalException(CodeMsg.SERVER_ERROR);
            }
            //判断手机号是否存在
            String mobile = loginVo.getMobile();
            String formpass = loginVo.getPassword();
            MiaoshaUser user = getById(Long.parseLong(mobile));
            if (user == null) {
                throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

            }
            //验证密码
            String dbPass = user.getPassword();
            String saltDB = user.getSalt();
            String calcPass = MD5Util.formPassToDBPass(formpass, saltDB);
            if (!calcPass.equals(dbPass)) {
                throw new GlobalException(CodeMsg.PASSWORD_ERROR);

            }

            String token = UUIDUtil.uuid();
            addCookie(response, token, user);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie=new Cookie(Cookie_Name_token,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);

        //延长有效期

        if(user != null) {
            addCookie(response, token, user);
        }
        return user;

    }
}
