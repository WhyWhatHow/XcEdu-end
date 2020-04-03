package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageRemotePostResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitMQConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
//import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class CmsPageService {
    @Autowired
    CmsPageRepository repository;

    /**
     * siteId 精确查询,
     * templateId 精确查询
     * pageAliase 模糊查询
     * 返回查询列表，
     *
     * @return com.xuecheng.framework.domain.cms.response.QueryPageResponse
     * @Author whywhathow
     * @Param [page, size, queryPageRequest] 页号，页面大小，查询请求参数
     **/
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            // 排除查询条件为空的情况
            queryPageRequest = new QueryPageRequest();
        }
        if (page < 1) {
            // 前端页码显示从1开始，后端页码从零开始
            page = 0;
        } else {
            page--;
        }
        if (size < 1) {
            size = 10;
        }
        //分页对象
        CmsPage cmspage = new CmsPage(queryPageRequest);
        Pageable pageable = PageRequest.of(page, size);
        //exampleMatcher 表示匹配器,
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                // 创建匹配对象//GenericPropertymatchers 默认匹配规则
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        // example 表示查询条件
        Example<CmsPage> example = Example.of(cmspage, exampleMatcher);
        Page<CmsPage> all = repository.findAll(example, pageable);
        //分页查询
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    public CmsPageResult addPage(CmsPage cmsPage) {

        if (cmsPage == null) {
            // 提交页面不会为空,前端进行表单校验,另外,不会有不带参数的请求到后端,因为需要require
            RuntimeExceptionCast.cast(CmsCode.CMS_UNLEGAL_PARAMSINPURL);
        }
        // 1. 判断页面是否已经存在
        CmsPage check = repository.findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        if (check != null) {
            // 已经存在该页面
            RuntimeExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        // 避免主键不一致的问题
        cmsPage.setPageId(null);
        CmsPage save = repository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, save);

    }

    public CmsPageResult editPage(String id, CmsPage cmsPage) {
        CmsPageResult res = findByPageId(id);
        if (!res.isSuccess()) {
            RuntimeExceptionCast.cast(CmsCode.CMS_UNLEGAL_PARAMSINPURL);
        }
        CmsPage upload = res.getCmsPage();

        upload.setTemplateId(cmsPage.getTemplateId());
        //更新所属站点
        upload.setSiteId(cmsPage.getSiteId());
        //更新页面别名
        upload.setPageAliase(cmsPage.getPageAliase());
        //更新页面名称
        upload.setPageName(cmsPage.getPageName());
        //更新访问路径
        upload.setPageWebPath(cmsPage.getPageWebPath());
        //更新物理路径
        upload.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

        upload.setDataUrl(cmsPage.getDataUrl());

        repository.save(upload);
        return new CmsPageResult(CommonCode.SUCCESS, upload);
    }

    public CmsPageResult findByPageId(String pageId) {
        Optional<CmsPage> byId = repository.findById(pageId);
        if (!byId.isPresent()) {
            // 先处理异常,然后进入正常步骤处理,避免无返回值的情况
            RuntimeExceptionCast.cast(CmsCode.CMS_UNLEGAL_PARAMSINPURL);
        }
        CmsPage cmsPage = byId.get();
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);

    }

    public ResponseResult delPage(String id) {
        CmsPageResult res = findByPageId(id);
        if (!res.isSuccess()) {
            RuntimeExceptionCast.cast(CmsCode.CMS_UNLEGAL_PARAMSINPURL);
        }

        repository.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Autowired
    PagePreviewService pagePreviewService;

    /**
     * 页面发布
     * 1. 获取静态页面 -html
     * 2. 将静态页面存入 gridFs中
     * 3. 更新cmsPage 页面信息:htmlFIleId, 保存
     * 4. 发送消息给交换机,结束
     **/
    public ResponseResult postPage(String pageId) {
        // 1. 获取静态化后html数据
        String html = pagePreviewService.getPreviewPageByPageId(pageId);
        if (StringUtils.isEmpty(html)) {
            // 排除生成页面为空的情况
            RuntimeExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        CmsPageResult res = findByPageId(pageId);

        CmsPage cmsPage = res.getCmsPage();
        // 2. 存储html 到gridFs中
        cmsPage = saveHTMLToGridFS(html, cmsPage);
        // 3. 跟新cmsPage信息, 保存htmlfileID
        cmsPage = repository.save(cmsPage);
        // 4. 发送消息到rabbitmq中
        sendMessageToRabbitMQ(cmsPage);
        //todo
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    // todo ,
    // 将消息发送给MQ
    private void sendMessageToRabbitMQ(CmsPage cmsPage) {
        String s = JSON.toJSONString(cmsPage);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(), s);
    }

    @Autowired
    GridFsTemplate gridFsTemplate;

    // 将生成的html文件保存到GridFs中
    private CmsPage saveHTMLToGridFS(String html, CmsPage page) {
        try {
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            ObjectId objectId = gridFsTemplate.store(inputStream, page.getPageName(), "utf-8");
            if (objectId == null) {
                RuntimeExceptionCast.cast(CmsCode.CMS_GRIDFS_STORE_HTML_FAIL);
            }
            page.setHtmlFileId(objectId.toHexString());
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CmsPageResult savePage(CmsPage page) {
        CmsPage now = repository.findBySiteIdAndPageNameAndPageWebPath(page.getSiteId(), page.getPageName(), page.getPageWebPath());
        CmsPageResult res = null;
        if (now == null) {
            // 当前并没有该页面, 则添加新页面
            res = this.addPage(page);
        } else {
            // 已存在页面,则更新页面
            res = this.editPage(now.getPageId(), page);
        }
        return res;
    }

    @Autowired
    CmsSiteRepository siteRepository;

    public CmsPageRemotePostResult postPageQuick(CmsPage page) {
        //1 .保存页面信息
        CmsPageResult cmsPageResult = this.savePage(page);
        if (!cmsPageResult.isSuccess()) {
            RuntimeExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmsPage = cmsPageResult.getCmsPage();
        String pageId = cmsPage.getPageId();
        // 2. 发布页面
        ResponseResult responseResult = postPage(pageId);
        // 3. 返回页面的url= siteDomain + siteWebPath+ pageWebPath + pageName
        String siteUrl = this.findSiteUrlByCmsPage(cmsPage);

        String url = siteUrl + cmsPage.getPageWebPath() + cmsPage.getPageName();
        return new CmsPageRemotePostResult(CommonCode.SUCCESS, url);
    }

    private String findSiteUrlByCmsPage(CmsPage page) {
        if (page == null || StringUtils.isEmpty(page.getSiteId())) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CmsSite> opt = siteRepository.findById(page.getSiteId());
        if (opt.isPresent()) {
            CmsSite cmsSite = opt.get();
            return cmsSite.getSiteDomain() + cmsSite.getSiteWebPath();
        }
        return null;
    }
}
