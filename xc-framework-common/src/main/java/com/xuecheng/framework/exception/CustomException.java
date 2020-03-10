package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
/***
 * @Author whywhathow
 * 自定义的运行时异常,
 * 异常信息包含有code,message
 **/
public class CustomException extends RuntimeException {
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        super("Error Code: "+resultCode.code()+" ; Error Information: "+resultCode.message());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
