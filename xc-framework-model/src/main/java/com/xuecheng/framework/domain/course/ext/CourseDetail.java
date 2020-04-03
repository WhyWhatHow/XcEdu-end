package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;

import java.io.Serializable;

/**
 * @program: XcEduCode
 * @description: 页面详情页的封装类
 * @author: WhyWhatHow
 * @create: 2020-03-26 15:33
 **/
public class CourseDetail implements Serializable {
    private CourseBase courseBase;

    @Override
    public String toString() {
        return "CourseDetail{" +
                "courseBase=" + courseBase +
                ", courseMarket=" + courseMarket +
                ", coursePic=" + coursePic +
                ", teachplanNode=" + teachplanNode +
                '}';
    }

    public CourseDetail(CourseBase courseBase, CourseMarket courseMarket, CoursePic coursePic, TeachplanNode teachplanNode) {
        this.courseBase = courseBase;
        this.courseMarket = courseMarket;
        this.coursePic = coursePic;
        this.teachplanNode = teachplanNode;
    }

    public CourseDetail() {
    }

    public CourseBase getCourseBase() {
        return courseBase;
    }

    public void setCourseBase(CourseBase courseBase) {
        this.courseBase = courseBase;
    }

    public CourseMarket getCourseMarket() {
        return courseMarket;
    }

    public void setCourseMarket(CourseMarket courseMarket) {
        this.courseMarket = courseMarket;
    }

    public CoursePic getCoursePic() {
        return coursePic;
    }

    public void setCoursePic(CoursePic coursePic) {
        this.coursePic = coursePic;
    }

    public TeachplanNode getTeachplanNode() {
        return teachplanNode;
    }

    public void setTeachplanNode(TeachplanNode teachplanNode) {
        this.teachplanNode = teachplanNode;
    }

    private CourseMarket courseMarket;
    private CoursePic coursePic;
    private TeachplanNode teachplanNode;

}
