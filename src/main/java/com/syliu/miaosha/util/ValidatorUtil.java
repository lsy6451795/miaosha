package com.syliu.miaosha.util;



import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private  static Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return  false;
        }
        Matcher m=mobile_pattern.matcher(src);
        return m.matches();
    }

    public static void main(String[] args) {

        System.out.println(isMobile("15000197379"));
    }
}
