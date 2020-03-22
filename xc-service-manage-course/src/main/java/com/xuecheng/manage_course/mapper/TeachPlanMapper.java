package com.xuecheng.manage_course.mapper;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeachPlanMapper {
    TeachplanNode selectTeachPlanByCourseId(String id);
    @Select("\n" +
            "SELECT id FROM teachplan WHERE courseid=#{id} AND parentid = '0'")
    String findIdByCourseIdAndParentID(String id);
}
