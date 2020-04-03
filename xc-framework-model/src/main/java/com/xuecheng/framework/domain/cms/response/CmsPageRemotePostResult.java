package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-29 13:44
 **/
@Data
@NoArgsConstructor
@ToString
public class CmsPageRemotePostResult extends ResponseResult {
    String url;

    public CmsPageRemotePostResult(ResultCode resultCode, String url) {
        super(resultCode);
        this.url = url;
    }

}
