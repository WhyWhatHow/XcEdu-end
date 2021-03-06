package com.xuecheng.framework.domain.media.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;


@ToString
public enum MediaCode implements ResultCode {
    UPLOAD_FILE_REGISTER_FAIL(false,22001,"上传文件在系统注册失败，请刷新页面重试！"),
    UPLOAD_FILE_REGISTER_EXIST(false,22002,"上传文件在系统已存在！"),
    UPLOAD_FILE_REGISTER_CREATE_FILEPATH_FAIL(false,22003,"上传文件失败,创建文件路径有误,请重试"),
    UPLOAD_FILE_REGISTER_FILE_IS_NULL(false,22004,"上传文件不存在"),

    CHUNK_FILE_EXIST_CHECK(true,22013,"分块文件在系统已存在！"),

    CHECK_MD5_FAIL(false,22100,"合并文件校验MD5失败"),

    SEND_MESSAGE_ERROR(false,22200,"发送消息失败,fileId=null"),

    RECEIVE_MESSAGE_ERROR(false ,22201,"接收到消息,消息为空,请重新发送"),

    SEARCH_MEDIA_ERROR(false,22300,"查询课程媒资信息失败,请重试"),

    VIDEO_M3U8_ERROR(false,22300,"生成视频m3u8错误"),
    VIDEO_MP4_ERROR(false,223001,"转换视频mp4出错"),

    MERGE_FILE_FAIL(false,22024,"合并文件失败，文件在系统已存在！"),
    MERGE_FILE_CHECKFAIL(false,22025,"合并文件校验失败！");

    //操作代码
    @ApiModelProperty(value = "媒资系统操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "媒资系统操作代码", example = "22001", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "媒资系统操作提示", example = "文件在系统已存在！", required = true)
    String message;
    private MediaCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, MediaCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, MediaCode> builder = ImmutableMap.builder();
        for (MediaCode commonCode : values()) {
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
