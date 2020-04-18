package com.xuecheng.manage_media_process.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @program: XcEduCode
 * @description: 媒资处理服务
 * @author: WhyWhatHow
 * @create: 2020-04-18 10:27
 **/
@Service
public class MediaVideoProcessService {
    private static Logger logger = LoggerFactory.getLogger(MediaVideoProcessService.class);
    @Autowired
    MediaFileRepository repository;

    // 处理视频的总步骤
    public void dealVideo(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            RuntimeExceptionCast.cast(MediaCode.RECEIVE_MESSAGE_ERROR);
        }
        Optional<MediaFile> opt = repository.findById(fileId);
        if (!opt.isPresent()) {
            RuntimeExceptionCast.cast(MediaCode.RECEIVE_MESSAGE_ERROR);
        }
        MediaFile mediaFile = opt.get();
        dealWithMimeType(mediaFile.getMimeType(), mediaFile);

    }


    private void dealWithMimeType(String mimeType, MediaFile mediaFile) {
        if (mimeType.contains("avi")) {
            // 处理 avi ,先生成mp4 ,在转换生成m3u8视频文件
            mediaFile.setProcessStatus("303001");
            repository.save(mediaFile);
            processAviVideoToM3U8(mediaFile);
        } else if (mimeType.contains("mp4")) {
            mediaFile.setProcessStatus("303001");
            repository.save(mediaFile);
            processMp4ToM3U8(mediaFile);
            // 处理 mp4格式
        } else {
            mediaFile.setProcessStatus("303004"); // 视频不处理
            repository.save(mediaFile);
        }
        return;
    }
    // 处理mp4视频
    private void processMp4ToM3U8(MediaFile mediaFile) {
        String m3u8Name = mediaFile.getFileId() + ".m3u8";
        String mPath = videoPath + mediaFile.getFilePath()+ "hls/";
        String mp4Name = mediaFile.getFileId() + ".mp4";
        String mp4FolderPath = videoPath+mediaFile.getFilePath();
        String videoMp4Path = mp4FolderPath+mediaFile.getFileName();
        processMp4ToM3U8(mediaFile,m3u8Name,mPath,videoMp4Path,new MediaFileProcess_m3u8());
    }

    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpegPath;

    @Value("${xc-service-manage-media.video-location}")
    String videoPath;

    /**
     * 1 . avi -> mp4
     * 2 . check mp4  generate successfully
     * 3 . mp4 --> m3u8
     * 4 . write message to mongoDB
     * @param mediaFile
     */
    private void processAviVideoToM3U8(MediaFile mediaFile) {
        String m3u8Name = mediaFile.getFileId() + ".m3u8";
        String mPath = videoPath + mediaFile.getFilePath()+ "hls/";
        String mp4Name = mediaFile.getFileId() + ".mp4";
        String mp4FolderPath = videoPath+mediaFile.getFilePath();
        String videoMp4Path = mp4FolderPath+mediaFile.getFileName();
        MediaFileProcess_m3u8 process_m3u8 = new MediaFileProcess_m3u8();
        // 1. avi -> mp4
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, videoMp4Path, mp4Name, mp4FolderPath);
        String mp4Res = mp4VideoUtil.generateMp4();
        if (StringUtils.isEmpty(mp4Res) || !mp4Res.equals("success")) {
            // 转化失败,记录日志,写入MongoDB
            dealWithErrorInGenerateVideo(mediaFile, process_m3u8, mp4Res);
            logger.error("generate mp4 video fail ...............");
            RuntimeExceptionCast.cast(MediaCode.VIDEO_MP4_ERROR);
        }
        // 3 generate hls file, 4 write to mongoDB
        processMp4ToM3U8(mediaFile, m3u8Name, mPath, videoMp4Path, process_m3u8);
    }

    private void processMp4ToM3U8(MediaFile mediaFile, String m3u8Name, String mPath, String videoMp4Path, MediaFileProcess_m3u8 process_m3u8) {
        // 3 generate hls file
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpegPath, videoMp4Path, m3u8Name, mPath);
        String res = hlsVideoUtil.generateM3u8();
        List<String> list = hlsVideoUtil.get_ts_list();
        if (StringUtils.isEmpty(res) || !res.equals("success") || list == null || list.size() == 0) {
            dealWithErrorInGenerateVideo(mediaFile,process_m3u8,res);
            logger.error("error in generate m3u8 video file.............");
            RuntimeExceptionCast.cast(MediaCode.VIDEO_M3U8_ERROR);
        }
        // 4. write to mongoDB
        process_m3u8.setTsList(list);
        mediaFile.setMediaFileProcess_m3u8(process_m3u8);
        mediaFile.setProcessStatus("303002");//处理成功
        mediaFile.setFileUrl(mediaFile.getFilePath()+"hls/"+mediaFile.getFileName());
        repository.save(mediaFile);
    }

    /**
     *  处理视频转换失败的结果,写入MongoDB
     * @param mediaFile
     * @param process_m3u8
     * @param errMsg
     */
    private void dealWithErrorInGenerateVideo(MediaFile mediaFile, MediaFileProcess_m3u8 process_m3u8, String errMsg) {
        mediaFile.setProcessStatus("303003");
        process_m3u8.setErrormsg(errMsg);
        mediaFile.setMediaFileProcess_m3u8(process_m3u8);
        repository.save(mediaFile);
    }
}
