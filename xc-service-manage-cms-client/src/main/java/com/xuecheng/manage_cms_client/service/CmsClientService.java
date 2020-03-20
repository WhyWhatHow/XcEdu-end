package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.manage_cms_client.mq.ConsumerPostPage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class CmsClientService {
    /**
     * 将gridFs 中的html download 到server 上
     * 页面路径 = 页面物理路径+web路径
     *
     * @param page
     */
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;

    private static final Logger LOGGER = LoggerFactory.getLogger(CmsClientService.class);

    public void PostPage(CmsPage page) {
        // 获取页面存储路径
        String pageWebPath = page.getPageWebPath();
        if (StringUtils.isEmpty(pageWebPath)) {
            LOGGER.error("error in cmsClient.postPage, webPath is NULL");
            RuntimeExceptionCast.cast(CmsCode.CMS_POSTPAGE_WEBPATHISNULL);
        }
        String pagePhysicalPath = page.getPagePhysicalPath();
        if (StringUtils.isEmpty(pagePhysicalPath)) {
            LOGGER.error("error in cmsClient.postPage, physicalPath is NULL");
            RuntimeExceptionCast.cast(CmsCode.CMS_POSTPAGE_PHYSICALPATHISNULL);
        }

        String path = pagePhysicalPath + pageWebPath;
        // 获取下载流
        InputStream stream = null;
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(path));
            stream = getDownLoadStream(page);
            IOUtils.copy(stream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取对应文件的下载流
     *
     * @param page
     * @return
     */
    private InputStream getDownLoadStream(CmsPage page) {
        String htmlFileId = page.getHtmlFileId();
        if (StringUtils.isEmpty(htmlFileId)) {
            RuntimeExceptionCast.cast(CmsCode.CMS_POSTPAGE_HTMLFILEISNOTEXIST);
        }
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        GridFSDownloadStream stream = gridFSBucket.openDownloadStream(file.getObjectId());
        GridFsResource resource = new GridFsResource(file, stream);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }


}
