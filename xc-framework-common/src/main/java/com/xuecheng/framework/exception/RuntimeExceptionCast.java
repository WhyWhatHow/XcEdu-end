package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
/**
 * @Author whywhathow
 * 自定义异常抛出类,用于处理运行时的异常
 **/
public class RuntimeExceptionCast {
    public static void cast(ResultCode resultCode){
        throw  new CustomException(resultCode);
    }
}
