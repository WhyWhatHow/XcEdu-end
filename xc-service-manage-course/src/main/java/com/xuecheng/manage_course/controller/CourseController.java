package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseDetail;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XcEduCode
 * @description: 课程管理的controller 层
 * @author: WhyWhatHow
 * @create: 2020-03-22 12:18
 **/
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService service;

    @Override
    @GetMapping("/teachplan/list/{id}")
    public TeachplanNode findTeachPlanByCourseId(@PathVariable("id") String id) {
        return service.getTeachPlanByCourseId(id);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachPlan(@RequestBody Teachplan teachplan) {
        return service.addTeachPlan(teachplan);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return service.addCourseBase(courseBase);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return service.findCourseList(page, size, courseListRequest);
    }

    // course_base
    @Override
    @GetMapping("/coursebase/get/{id}")
    public CourseBase findCourseBaseByCourseId(@PathVariable("id") String courseId) {
        return service.findCourseBaseByCourseId(courseId);
    }

    @Override
    @PostMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBaseInfo(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return service.updateCourseBaseInfo(id, courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{id}")
    public CourseMarket findCourseMarketByCourseId(@PathVariable("id") String id) {
        return service.findmarketByCourseId(id);
    }

    @Override
    @PostMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarketInfo(@PathVariable("id") String id, @RequestBody CourseMarket market) {

        CourseMarket one = service.updateCourseMarketInfo(id, market);
        if (one == null) {
            return new ResponseResult(CommonCode.FAIL);
        } else {
            return new ResponseResult(CommonCode.SUCCESS);
        }
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic") String fileId) {
        return service.addCoursePic(courseId,fileId);
    }

    @Override
    @GetMapping("/coursepic/list/{id}")
    public CoursePic findCoursePictureByCourseId(@PathVariable("id") String courseId) {
        return service.findCoursePicByCourseId(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePicByCourseId(@RequestParam  String courseId) {
        return service.deleteCoursePicById(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseDetail getCourseDetailByCourseId(@PathVariable("id") String courseId) {
        return service.getCourseDetailById(courseId);
        //        return null;
    }

    @Override
    @GetMapping("/preview/{id}")
    public CoursePreviewResult previewCourseDetail(@PathVariable("id") String courseId) {
        return service.previewCourseDetail(courseId);
    }

    @Override
    @GetMapping("/publish/{id}")
    public CoursePublishResult publishCourse(@PathVariable("id") String courseId) {
        return service.publishCourseDetail(courseId);
   }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return service.saveMedia(teachplanMedia);
    }


    //course_market

}
