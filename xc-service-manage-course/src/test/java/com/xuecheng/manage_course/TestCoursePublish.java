package com.xuecheng.manage_course;

import com.xuecheng.manage_course.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-07 12:16
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCoursePublish {
    @Autowired
    CourseService service;
    @Test
    public  void test(){
        String id = "4028e581617f945f01617f9dabc40000";
        service.publishCourseDetail(id);
    }
}
