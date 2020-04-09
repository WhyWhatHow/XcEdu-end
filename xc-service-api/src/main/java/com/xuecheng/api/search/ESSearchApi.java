package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.naming.directory.SearchResult;

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

}
