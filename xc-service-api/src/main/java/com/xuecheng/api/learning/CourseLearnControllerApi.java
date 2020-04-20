package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.response.MediaUrlResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XcEduCode
 * @description: 课程学习接口
 * @author: WhyWhatHow
 * @create: 2020-04-19 20:32
 **/
@Api("课程学习中心接口")
public interface CourseLearnControllerApi {

    @ApiOperation("根据教学计划id获取mediaUrl")
    public MediaUrlResult getMediaUrl(String courseId,String id);
}
