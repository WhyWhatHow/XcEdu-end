package com.xuecheng.search.controller;

import com.xuecheng.api.search.ESSearchApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getAll(@PathVariable("id") String id) {
        return service.getAll(id);
    }

    @Override
    @GetMapping("/getmedia/{id}")
    public TeachplanMediaPub findTeachPlanMediaPubByID(@PathVariable  String id) {
        String[] arr =new String[]{id};
        QueryResponseResult queryResponseResult = service.findTeachPlanMediaPub(arr);
        List list = queryResponseResult.getQueryResult().getList();
        long total = queryResponseResult.getQueryResult().getTotal();
        if(!queryResponseResult.isSuccess()||total==0 || list==null||list.size()==0){
            return null;
        }
        return (TeachplanMediaPub) list.get(0);
    }
}
