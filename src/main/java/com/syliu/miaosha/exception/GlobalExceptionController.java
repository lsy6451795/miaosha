package com.syliu.miaosha.exception;

import com.syliu.miaosha.Result.CodeMsg;
import com.syliu.miaosha.Result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeoutException;


@ControllerAdvice//切面拦截异常
@ResponseBody
public class GlobalExceptionController {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }
        else if(e instanceof BindException){
            BindException ex=(BindException) e;
            List<ObjectError> errors=ex.getAllErrors();
            ObjectError error=errors.get(0);
            String msg=error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else if(e instanceof TimeoutException){

            return Result.error(CodeMsg.OVER_VISIT);
        }
        else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }


}
