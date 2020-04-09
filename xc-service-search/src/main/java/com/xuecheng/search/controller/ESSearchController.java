package com.xuecheng.search.controller;

import com.xuecheng.api.search.ESSearchApi;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-07 14:53
 **/
@RestController
@RequestMapping("/search/course")
public class ESSearchController implements ESSearchApi {
    @Autowired
    EsSearchService service ;
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult searchCourse(@PathVariable("page") int page,@PathVariable("size") int size, CourseSearchParam param) {
//        return null;
        return service.searchCourse(page,size,param);
    }
}
