package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/2/10.
 */
@Data
@ToString
//@Component
public class CourseInfo extends CourseBase {

    //课程图片
    private String pic;

}
