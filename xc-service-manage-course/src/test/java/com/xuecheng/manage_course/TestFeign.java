package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {
//    @Autowired
//    RestTemplate restTemplate;
    @Autowired
    CmsPageClient client ;

    @Test
    public void testFeign(){
        String id = "5a754adf6abb500ad05688d9";
        CmsPageResult pageByPageId = client.getPageByPageId(id);
        System.out.println(pageByPageId);
    }


}
