package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePictureRepository extends JpaRepository<CoursePic,String> {
    long deleteCoursePicByCourseid(String id);
}
