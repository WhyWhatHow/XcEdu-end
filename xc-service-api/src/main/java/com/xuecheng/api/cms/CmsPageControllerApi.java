package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageRemotePostResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

//TODO  查询: pageName 模糊查询, pageType :精确查询

@Api(value = "cms页面管理接口", tags = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表") //done
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"), @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation("添加页面") //done
    public CmsPageResult addPage(CmsPage cmsPage);

    @ApiOperation("根据id查找页面")
    public CmsPageResult findByPageId(String pageId);

    @ApiOperation("修改页面")
    public CmsPageResult editPage(String id , CmsPage cmsPage);
    @ApiOperation("删除页面")
    public ResponseResult delPage(String id);
    @ApiOperation("发布页面")
    public ResponseResult postPage(String pageId);
    @ApiOperation("页面存在,则更新页面,页面不存在,则添加页面")
    public  CmsPageResult savePage(CmsPage page);

    @ApiOperation("一键发布页面")
    public CmsPageRemotePostResult quickPost(CmsPage page);
}
