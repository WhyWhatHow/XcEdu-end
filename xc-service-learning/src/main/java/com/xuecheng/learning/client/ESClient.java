package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: XcEduCode
 * @description: 搜索服务客户端
 * @author: WhyWhatHow
 * @create: 2020-04-19 21:01
 **/
@FeignClient (value = "XC-SEARCH-SERVICE")
public interface ESClient {
    @GetMapping("/search/course/getmedia/{id}")
    // 通过教案获取媒资信息
    public TeachplanMediaPub findMeidaByTpId(@PathVariable("id") String id);

}
