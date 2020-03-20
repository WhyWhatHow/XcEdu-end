package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PagePreviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Api("页面预览界面")
@RestController
@RequestMapping("/cms/preview/")
public class CmsPagePreviewController  extends BaseController {
    @Autowired
    PagePreviewService service;

    @ApiOperation("获取预览页面")
    @GetMapping("{pageId}")
    public  void  getPreviewPage(@PathVariable("pageId") String id){

        String previewPage = service.getPreviewPageByPageId(id);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(previewPage.getBytes("utf-8"));
        } catch (IOException e) {
            // todo 处理获取preview 页面时的读写异常
            e.printStackTrace();
        }

    }
}
