package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-18 15:21
 **/
@Api(value="媒资文件管理接口",  description="媒资文件的CUID")
public interface MediaFileControllerApi {
    @ApiOperation("分页查询媒资文件")
    public QueryResponseResult queryList(int page, int size , QueryMediaFileRequest request);

    // todo
    @ApiOperation("删除媒资文件" )
    public ResponseResult deleteFieByID(String fileMd5);

    @ApiOperation("修改媒资信息")
    public ResponseResult editMediaFile();

    @ApiOperation("处理媒资文件")
    public ResponseResult processFIle(String fileMd5);
}
