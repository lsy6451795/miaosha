package com.syliu.miaosha.validator;

import com.syliu.miaosha.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean requied=true;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
       requied= constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(requied){
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s)){
                return true;
            }else return ValidatorUtil.isMobile(s);
        }

    }
}
