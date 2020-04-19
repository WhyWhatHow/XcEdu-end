package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.naming.directory.SearchResult;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description: 课程查询接口
 * @author: WhyWhatHow
 * @create: 2020-04-04 17:11
 **/
@Api(value = "Elastic Search 查询接口",description = "只查询索引结果")
public interface ESSearchApi {
    @ApiOperation("课程查询, ES index")
    public QueryResponseResult searchCourse(int page , int size
     , CourseSearchParam param);

    @ApiOperation("根据课程ID查询课程的详细信息")
    public Map<String,CoursePub> getAll(String id );

    @ApiOperation("根据教学计划ID查询教案视频连接")
    public TeachplanMediaPub findTeachPlanMediaPubByID(String id);
}
