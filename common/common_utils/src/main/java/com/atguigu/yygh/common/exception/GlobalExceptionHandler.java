package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.utils.ExceptionUtil;
import com.atguigu.yygh.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e){
//        e.printStackTrace();
        log.error(ExceptionUtil.getMessage(e));
        return Result.error().message(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result exceptionHandler(NullPointerException e){
//        e.printStackTrace();
        log.error(ExceptionUtil.getMessage(e));
        return Result.error().message("空指针异常");
    }

    /**
     * id 小于0
     */
    @ExceptionHandler(YYGHException.class)
    public Result exceptionHandler(YYGHException e){
//        e.printStackTrace();
        log.error(ExceptionUtil.getMessage(e));
        return Result.error().code(e.getCode()).message(e.getMessage());
    }


}
