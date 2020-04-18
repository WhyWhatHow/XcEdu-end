package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaFileService;
import com.xuecheng.manage_media.service.UploadMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description: 媒资文件管理接口
 * @author: WhyWhatHow
 * @create: 2020-04-18 15:26
 **/
@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {
    @Autowired
    MediaFileService service;

    @Override
    @GetMapping("/list/{page}/{size}/")
    public QueryResponseResult queryList(@PathVariable int page,@PathVariable int size, QueryMediaFileRequest request) {
           return  service.queryList(page,size,request);

    }

    @Override
    public ResponseResult deleteFieByID(String fileMd5) {
        return null;
    }

    @Override
    public ResponseResult editMediaFile() {
        return null;
    }

    @Autowired
    UploadMediaService uploadMediaService;
    @Override
    @GetMapping("/process/{id}")
    public ResponseResult processFIle(@PathVariable("id") String fileMd5) {
        return uploadMediaService.processVideoFile(fileMd5);

    }
}
