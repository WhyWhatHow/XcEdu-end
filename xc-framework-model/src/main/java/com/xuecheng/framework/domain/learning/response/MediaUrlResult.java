package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: XcEduCode
 * @description: 媒资信息结果
 * @author: WhyWhatHow
 * @create: 2020-04-19 20:36
 **/
@Data
@ToString
@NoArgsConstructor
public class MediaUrlResult extends ResponseResult {
    public MediaUrlResult(ResultCode code, String url){
        super(code);
        this.mediaUrl = url;
    }
String mediaUrl ;
}
