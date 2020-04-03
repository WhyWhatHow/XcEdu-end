package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseDetail;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程管理接口",description = "提供课程的curd")
public interface CourseControllerApi {
    @ApiOperation("通过课程id查询课程列表")
    public TeachplanNode findTeachPlanByCourseId(String id);
    @ApiOperation("添加课程计划")
    public ResponseResult addTeachPlan(Teachplan teachplan);
    @ApiOperation("添加课程")
    public AddCourseResult addCourseBase(CourseBase courseBase);
    @ApiOperation("获取我的课程列表")
    public QueryResponseResult findCourseList(int page , int size , CourseListRequest courseListRequest);

    @ApiOperation("根据课程id获取课程基本信息")
    public CourseBase findCourseBaseByCourseId(String courseId);
    @ApiOperation("修改课程基本信息")
    public ResponseResult  updateCourseBaseInfo(String id, CourseBase courseBase);

    @ApiOperation("根据课程id获取课程营销信息")
    public CourseMarket findCourseMarketByCourseId(String id);
    @ApiOperation("修改或者添加课程营销信息")
    public ResponseResult updateCourseMarketInfo(String id,CourseMarket market);

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId,  String fileId);
    @ApiOperation("根据课程Id查询图片")
    public CoursePic findCoursePictureByCourseId(String couseId);
    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePicByCourseId(String courseId);

    @ApiOperation("获取课程详情页面")
    public CourseDetail getCourseDetailByCourseId(String courseId);
    @ApiOperation("课程详情页面预览")
    public CoursePreviewResult previewCourseDetail(String courseId);

    @ApiOperation("发布课程")
    public CoursePublishResult publishCourse(String courseId );
}
