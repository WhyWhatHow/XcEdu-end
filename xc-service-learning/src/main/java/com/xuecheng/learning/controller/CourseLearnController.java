package com.xuecheng.learning.controller;

import com.xuecheng.api.learning.CourseLearnControllerApi;
import com.xuecheng.framework.domain.learning.response.MediaUrlResult;
import com.xuecheng.learning.service.CourseLearnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-19 20:41
 **/
@RestController
@RequestMapping("/learning/course")
public class CourseLearnController implements CourseLearnControllerApi {

    @Autowired
    CourseLearnService service;

    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    public MediaUrlResult getMediaUrl(@PathVariable("courseId") String courseId, @PathVariable("teachplanId") String id) {
        return service.getMediaUrl(courseId, id);
    }

}
