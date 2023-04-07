package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e){
        e.printStackTrace();
        return Result.error().message(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result exceptionHandler(NullPointerException e){
        e.printStackTrace();
        return Result.error().message("空指针异常");
    }

    /**
     * id 小于0
     */
    @ExceptionHandler(LtZeroException.class)
    public Result exceptionHandler(LtZeroException e){
        e.printStackTrace();
        return Result.error().code(e.getCode()).message(e.getMessage());
    }


}
