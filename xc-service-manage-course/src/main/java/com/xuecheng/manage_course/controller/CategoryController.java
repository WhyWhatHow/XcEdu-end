package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-22 13:26
 **/
@RestController
@RequestMapping("/category")
public class CategoryController implements CategoryControllerApi {
    @Autowired
    CategoryService service ;
    @Override
    @GetMapping("/list")
    public CategoryNode findAll() {
        return  service.findAll();
    }
}
