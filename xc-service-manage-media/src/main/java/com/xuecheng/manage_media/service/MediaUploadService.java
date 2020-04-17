package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-17 12:37
 **/
@Service
public class MediaUploadService {
    public static Logger logger = LoggerFactory.getLogger(Exception.class);
    @Autowired
    MediaFileRepository repository;

    @Value("${xc-service-manage-media.upload-location}")
    String uploadLocation;// 文件上传路径

    /**
     * 获取文件路径
     *
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFilePath(String fileMd5, String fileExt) {
        if (StringUtils.isEmpty(fileMd5) || StringUtils.isEmpty(fileExt)) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(uploadLocation).append("/").
                append(fileMd5.substring(0, 1)).
                append("/").
                append(fileMd5.substring(1, 2))
                .append("/").append(fileMd5.substring(2)).append(".").append(fileExt);
        return buffer.toString();
    }

    // 返回文件相对路径
    private String getFileRelativePath(String fileMd5, String fileExt) {
        String relationPath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5.substring(2) + "." + fileExt;
        return relationPath;
    }

    // 返回文件目录
    private String getFileFolder(String fileMd5) {
        String fileFolder = uploadLocation + "/" + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/";
        return fileFolder;
    }

    // 创建文件目录
    private boolean createFilePath(String fileMd5) {
        String fileFolder = getFileFolder(fileMd5);
        File file = new File(fileFolder);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();
    }

    /**
     * 1 检查文件是否上传，已上传则直接返回。
     * 2 检查文件上传路径是否存在，不存在则创建。
     * 3 创建文件分块目录
     *
     * @param fileMd5 根据文件的md5获取文件的路径
     *                md5 第一个字符 一级目录
     *                md5 第2个字符   2级目录
     *                md5 余下字符    文件名
     * @param fileExt 文件扩展名
     * @return
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String fileType, String fileExt) {
        // 获取文件目录
        String filePath = getFilePath(fileMd5, fileExt);
        File fileFolder = new File(filePath);
        // 获取 数据库文件
        Optional<MediaFile> opt = repository.findById(fileMd5);
        if (fileFolder.exists() && opt.isPresent()) {
            // 如果文件存在 ,返回文件已存在
            RuntimeExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        // 不存在创建文件目录
        boolean filePath1 = createChunkFolder(fileMd5);
//        boolean filePath1 = createFilePath(fileMd5);
        if (!filePath1) {
            RuntimeExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //得到块文件所在目录
    private String getChunkFileFolderPath(String fileMd5) {
        String fileChunkFolderPath = getFileFolder(fileMd5) + "/chunks/";
        return fileChunkFolderPath;
    }

    /**
     * 检查分块文件是否上传，已上传则返回true。
     * 未上传则检查上传路径是否存在，不存在则创建。
     *
     * @param fileMd5
     * @param chunkIndex
     * @param chunkSize
     * @return
     */
    public CheckChunkResult checkFileChunk(String fileMd5, Integer chunkIndex, Long chunkSize) {
        // 获取分块所在路径 filePath+"/chunks/"
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String filePath = chunkFileFolderPath + chunkIndex;
        File file = new File(filePath);
        if (file.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        } else {
            return new CheckChunkResult(CommonCode.FAIL, false);
        }

    }

    /**
     * 将分块文件上传到指定路径
     *
     * @param file
     * @param chunkIndex
     * @param fileMd5
     * @return
     */
    public ResponseResult uploadChunk(MultipartFile file, Integer chunkIndex, String fileMd5) {
        if (file == null) {
            RuntimeExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FILE_IS_NULL);
        }
        // 获取文件块所在路径
        boolean res = createChunkFolder(fileMd5);
        // 创建块文件
        File chunkFile = new File(getChunkFileFolderPath(fileMd5) + chunkIndex);
        // 写 到 chunkFile
//        Byte[] buf =new Byte[1024];
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("upload chunk file fail",e.getMessage());
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 创建分块目录
    private boolean createChunkFolder(String fileMd5) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File file = new File(chunkFileFolderPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 合并文件
     * 校验文件md5是否正确
     * 向Mongodb写入文件信息
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param fileType
     * @param fileExt
     * @return
     */
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String fileType, String fileExt) {
        // 1 创建合并文件
        File mergeFile = new File(getFilePath(fileMd5, fileExt));
        boolean newFile = false;
        if (mergeFile.exists()) {
            mergeFile.delete();
        } else {
            try {
                newFile = mergeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("create new file error in merge chunk file",e.getMessage());
            }
        }
        if (!newFile) {
            RuntimeExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        // 2 获取分块文件
        File chunkFileFolder = new File(getChunkFileFolderPath(fileMd5));
        if (!chunkFileFolder.isDirectory()) {
            //  尚未获取到文件块
            RuntimeExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //3  合并文件
        mergeFile = merge(chunkFileFolder, mergeFile);
        // 4 校验md5
        boolean checkMd5 = checkFileMd5(fileMd5, mergeFile);
        if (!checkMd5) {
            RuntimeExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        // 5 保存到 mongoDB中
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        mediaFile.setFilePath(getFileRelativePath(fileMd5,fileExt));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(fileType);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = repository.save(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 校验文件的md5
    private boolean checkFileMd5(String fileMd5, File mergeFile) {
        if (StringUtils.isEmpty(fileMd5) || mergeFile == null) {
            RuntimeExceptionCast.cast(MediaCode.CHECK_MD5_FAIL);
        }
        boolean res = false;
        try {
            FileInputStream inputStream = new FileInputStream(mergeFile);
            String mergeFileMd5 = DigestUtils.md5Hex(inputStream);
            res  = mergeFileMd5.equalsIgnoreCase(fileMd5);
        } catch (Exception e) {
            logger.error("get mergeFile md5 Error",e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    //    合并文件
    private File merge(File chunkFileFolder, File mergeFile) {
        File[] files = chunkFileFolder.listFiles();
        // 保证 按chunk 块文件名 asc
        Arrays.sort(files, (f1, f2) -> {
            int first = Integer.parseInt(f1.getName());
            int second = Integer.parseInt(f2.getName());
            return first - second;
        });
        byte[] b = new byte[1024];
        try {
            RandomAccessFile writeRandomFile = new RandomAccessFile(mergeFile, "rw");
            for (File file : files) {
                RandomAccessFile readFile = new RandomAccessFile(file, "r");
                int len = -1;
                while ((len = readFile.read(b)) != -1) {
                    writeRandomFile.write(b, 0, len);
                }
                readFile.close();
            }
            writeRandomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mergeFile;

    }
}
