package com.xuecheng.manage_course.mapper;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    CategoryNode findAll();
}
