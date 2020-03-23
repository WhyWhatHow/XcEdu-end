package com.xuecheng.api.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件系统的接口", description="实现文件的上传,下载,修改,删除功能")
public interface FileSystemControllerApi {

    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile file, String fileTag, String businesskey,String metadata);

//    @ApiOperation("删除文件")
}
