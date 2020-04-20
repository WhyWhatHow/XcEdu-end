package com.xuecheng.framework.domain.learning.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
public enum LearnCode implements ResultCode {
    GET_MEDIA_URL_FALSE(false,31000,"获取mediaUrl 失败,请重试")




    ;

    //操作代码
    @ApiModelProperty(value = "学习系统操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "学习系统操作代码", example = "22001", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "学习系统操作提示", example = "文件在系统已存在！", required = true)
    String message;
    private LearnCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, LearnCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, LearnCode> builder = ImmutableMap.builder();
        for (LearnCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
