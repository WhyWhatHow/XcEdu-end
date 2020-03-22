package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.mapper.CategoryMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-22 12:54
 **/
@Service
public class CategoryService {
    @Autowired
    CategoryMapper mapper;
    public CategoryNode findAll() {
        return mapper.findAll();
    }
}
