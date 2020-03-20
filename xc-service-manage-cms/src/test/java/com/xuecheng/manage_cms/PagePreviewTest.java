package com.xuecheng.manage_cms;

import com.xuecheng.manage_cms.service.PagePreviewService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PagePreviewTest {

    @Autowired
    PagePreviewService service;
    @Test
    public void testGetHtml(){
        service.getPreviewPageByPageId("5e732027a3f94721344ba27d");
    }
}
