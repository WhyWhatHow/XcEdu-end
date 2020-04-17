package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-17 12:36
 **/
@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaFileUploadControllerApi {
    @Autowired
    MediaUploadService service;

    @Override
    @PostMapping("/register")
//    // TODO: 2020/4/17 消除无用参数,换成分装类 
    public ResponseResult register(@RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("fileName") String fileName,
                                   @RequestParam("fileSize") Long fileSize,
                                   @RequestParam("mimetype") String mimetype,
                                   @RequestParam("fileExt") String fileExt) {
        return service.register(fileMd5, fileName, fileSize, mimetype, fileExt);
    }

    @Override
    @PostMapping("/checkchunk")
    public CheckChunkResult checkFileChunk(@RequestParam("fileMd5") String fileMd5,
                                           @RequestParam("chunk") Integer chunk,
                                           @RequestParam("chunkSize") Long chunkSize) {
        return service.checkFileChunk(fileMd5, chunk, chunkSize);
    }

    @Override
    @PostMapping("/uploadchunk")
    public ResponseResult uploadChunk(@RequestParam("file") MultipartFile file,
                                      @RequestParam("chunk") Integer chunk,
                                      @RequestParam("fileMd5") String fileMd5) {
        return service.uploadChunk(file, chunk, fileMd5);
    }

    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                      @RequestParam("fileName") String fileName,
                                      @RequestParam("fileSize") Long fileSize,
                                      @RequestParam("mimetype") String mimetype,
                                      @RequestParam("fileExt") String fileExt) {
        return service.mergeChunks(fileMd5, fileName, fileSize, mimetype, fileExt);
    }
}
