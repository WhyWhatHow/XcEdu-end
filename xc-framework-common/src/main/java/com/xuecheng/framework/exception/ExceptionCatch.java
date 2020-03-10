package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionCatch {
    public static Logger logger = LoggerFactory.getLogger(Exception.class);

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    // 捕获运行时异常, CustomException
    public ResponseResult dealWithCustomException(CustomException e) {
        // 记录日志
        logger.error("catch runtimeException(can fix): ", e.getMessage(), e);
        // 返回提示信息
        ResultCode resultCode = e.getResultCode();
        System.err.println(resultCode);
        return new ResponseResult(resultCode);
    }

    // 处理框架,系统异常
    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult dealWithException(Exception e) {
        // log
        logger.error("catch exception :" + e.getMessage(), e);
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build() ;
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());


        if (resultCode != null) {
            return new ResponseResult(resultCode);
        } else {
            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }


    }
    static {
        //todo 框架异常的重新捕获+处理
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
        builder.put(HttpMediaTypeNotSupportedException.class, CommonCode.INVALID_PARAM);
        builder.put(HttpRequestMethodNotSupportedException.class,CommonCode.ILLEGAL_METHOD);
    }
}
