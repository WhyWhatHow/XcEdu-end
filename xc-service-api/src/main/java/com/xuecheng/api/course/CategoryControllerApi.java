package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程分类信息管理接口",description = "提供课程分类信息的curd")
public interface CategoryControllerApi {
    @ApiOperation("查询所有分类信息")
    public CategoryNode findAll();
}
