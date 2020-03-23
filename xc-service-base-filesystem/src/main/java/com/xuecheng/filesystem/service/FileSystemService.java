package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepostiory;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-22 23:43
 **/
@Service
@Transactional
public class FileSystemService {
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    void initFdfs(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            RuntimeExceptionCast.cast(FileSystemCode.FS_INIT_FDFS_FAIL);
        }
    }

    @Autowired
    FileSystemRepostiory repostiory ;
    public UploadFileResult upload(MultipartFile file, String fileTag, String businesskey, String metadata) {
        // 上传文件到fdfs中 ,返回文件id
        if(file ==null){
            // 上传文件为空
            RuntimeExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        initFdfs();
        String fileId = uploadToFDFS(file);
        if (StringUtils.isEmpty(fileId)) {
            // 上传失败
            RuntimeExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        // 将文件信息写入到mongodb中
        FileSystem fileSystem =new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setBusinesskey(businesskey);
        Map map = null;
        if(StringUtils.isNotEmpty(metadata)){
            map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        FileSystem save = repostiory.save(fileSystem);
        return  new UploadFileResult(CommonCode.SUCCESS,save);
    }

    /**
     * 上传文件到图片服务器
     * @param file
     * @return
     */
    private String uploadToFDFS(MultipartFile file) {
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient = new StorageClient1(trackerServer,storeStorageServer);
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')+1);
            String file1 = storageClient.upload_file1(file.getBytes(), ext, null);
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        String text = "";
//
//        Map map = JSON.parseObject(text, Map.class);
//        System.out.println(map.toString());
//    }
}
