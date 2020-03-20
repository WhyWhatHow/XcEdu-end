package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author John Nash
 */
@RestController
@RequestMapping(value = "/cms/page")
public class CmsPageController  implements CmsPageControllerApi {
    @Autowired
    CmsPageService cmsPageService;
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        return cmsPageService.findList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult addPage(@RequestBody  CmsPage cmsPage) {
        return cmsPageService.addPage(cmsPage);
    }

    @Override
    @GetMapping("/get/{pageId}")
    public CmsPageResult findByPageId(@PathVariable("pageId")  String pageId) {
        return cmsPageService.findByPageId(pageId);
    }


    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult editPage(@PathVariable("id") String id ,@RequestBody CmsPage cmsPage) {
        return cmsPageService.editPage(id,cmsPage);
    }

    @Override
    @GetMapping("/del/{id}")
    public ResponseResult delPage(@PathVariable("id") String id) {
//        return null;
        return cmsPageService.delPage(id);
    }

    @Override
    @GetMapping("/post/{id}")
    public ResponseResult postPage(@PathVariable("id") String pageId) {
        return cmsPageService.postPage(pageId);
    }
}
