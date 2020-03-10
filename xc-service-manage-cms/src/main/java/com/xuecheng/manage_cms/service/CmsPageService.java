package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
//            return new CmsPageResult(CommonCode.FAIL, null);//todo 异常统一处理
        }
        // 避免主键不一致的问题
        cmsPage.setPageId(null);
        repository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);

    }

    public CmsPageResult editPage(String id, CmsPage cmsPage) {
        CmsPageResult res = findByPageId(id);
        if (!res.isSuccess()) {
            RuntimeExceptionCast.cast(CmsCode.CMS_UNLEGAL_PARAMSINPURL);
        }
        repository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
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
}
