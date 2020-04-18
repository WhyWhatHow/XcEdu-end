package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.UploadMediaFileRequest;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: XcEduCode
 * @description: 媒资文件个管理接口
 * @author: WhyWhatHow
 * @create: 2020-04-17 12:23
 **/
@Api("媒资文件上传接口")
public interface UPloadMediaControllerApi {

    @ApiOperation("文件注册,校验文件目录是否已经存在等对应前端的webUploader.before-send-file")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String fileType, String fileExt);
//    public ResponseResult register(UploadMediaFileRequest request);


    @ApiOperation("文件上传分块检查")
    public CheckChunkResult checkFileChunk(String fileMd5, Integer chunkIndex, Long chunkSize);

    @ApiOperation("上传分块")
    public ResponseResult uploadChunk(MultipartFile file, Integer chunkIndex, String fileMd5);

    @ApiOperation("合并分块")
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String fileType, String fileExt);
//    public ResponseResult mergeChunks(UploadMediaFileRequest request);

}
