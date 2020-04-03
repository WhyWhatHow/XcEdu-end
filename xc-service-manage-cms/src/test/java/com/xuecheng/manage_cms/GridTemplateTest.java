package com.xuecheng.manage_cms;

import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.service.PagePreviewService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridTemplateTest {

    @Autowired
    GridFsTemplate gridFsTemplate;
// // TODO: 2020/3/26  尚未实现: 教育机构页面模板、教师信息页面模板、课程统计信息json模板、教育机构统计信息json模板 
    // 测试 fsgridFs 存文件
    @Test
    public void testGridFsStore() throws FileNotFoundException {
        // 对应文件的id 5e722999a3f9471b90334600
        File file = new File("d:/course.ftl");
        FileInputStream inputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(inputStream, "course.ftl");
        System.out.println(store);
//        5e732589a3f947301c2ac45b
    }

    @Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void testGridFsGetFile() throws IOException {
        // 获取文件信息
        Query query = Query.query(Criteria.where("_id").is("5e722999a3f9471b90334600"));
        GridFSFile file = gridFsTemplate.findOne(query);
        // 获取下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());

        GridFsResource resource = new GridFsResource(file,gridFSDownloadStream);

        // 从流中取数据
        String s = IOUtils.toString(resource.getInputStream(), "utf-8");
        System.out.println(s);


    }
}
