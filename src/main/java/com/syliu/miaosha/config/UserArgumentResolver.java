package com.syliu.miaosha.config;

import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.MiaoshaUserService;
import com.syliu.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Service
public class UserArgumentResolver  implements HandlerMethodArgumentResolver {
    @Autowired
    private MiaoshaUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?>clazz=methodParameter.getParameterType();
        return clazz== MiaoshaUser.class;
    }

    @Override
    public MiaoshaUser resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String cookietoken=getCookieValue(request,MiaoshaUserService.Cookie_Name_token);
        String paramtoken=request.getParameter(MiaoshaUserService.Cookie_Name_token);
        if(StringUtils.isEmpty(cookietoken)&&StringUtils.isEmpty(paramtoken))return null;
        String token=StringUtils.isEmpty(cookietoken)?paramtoken:cookietoken;

        return userService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request, String cookie_name_token) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0)return  null;
        for (Cookie cookie:cookies
             ) {
            if(cookie.getName().equals(cookie_name_token))return cookie.getValue();

        }
        return null;
    }


}
