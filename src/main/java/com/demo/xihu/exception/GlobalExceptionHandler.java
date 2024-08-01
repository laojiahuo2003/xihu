package com.demo.xihu.exception;

import com.demo.xihu.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 捕获业务异常
     * @param e
     * @return
     */
    @ExceptionHandler({BaseException.class,Exception.class})
    public Result handleException(Exception e){
        e.printStackTrace();//输出到后台
        return Result.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败");
    }


/*    *//**
     * 处理SQL异常
     * @param ex
     * @return
     *//*
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + "已存在";
            return Result.error(msg);
        }else{
            return Result.error("唯一键约束违反");
        }
    }*/

}
