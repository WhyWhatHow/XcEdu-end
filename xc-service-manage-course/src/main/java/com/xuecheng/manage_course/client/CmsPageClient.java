package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageRemotePostResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-26 09:59
 **/
@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //由feign生成代理对象
public interface CmsPageClient {
    @GetMapping("/cms/page/get/{id}")
    public CmsPageResult getPageByPageId(@PathVariable("id") String id );

    @PostMapping("/cms/page/save")
    public  CmsPageResult savePage(@RequestBody CmsPage page);

    @PostMapping("/cms/page/postPageQuick")
    public CmsPageRemotePostResult postPage(@RequestBody CmsPage page);
}
