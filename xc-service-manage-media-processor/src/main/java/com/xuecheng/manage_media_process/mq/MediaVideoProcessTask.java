package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.manage_media_process.service.MediaVideoProcessService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @program: XcEduCode
 * @description: 处理视频媒资文件
 * @author: WhyWhatHow
 * @create: 2020-04-17 17:06
 **/
@Component
public class MediaVideoProcessTask {

    @Value("${xc-service-manage-media.video-location}")
    String videoLocation;
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpagPath;
    @Autowired
    MediaVideoProcessService service;

    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}", containerFactory ="consumerContainerFactory" )
    public void dealWithMedia(String msg) {
        MediaFile mediaFile = JSON.parseObject(msg, MediaFile.class);
        service.dealVideo(mediaFile.getFileId());
    }

}
