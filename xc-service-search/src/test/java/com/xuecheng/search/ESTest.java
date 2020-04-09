package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description: 测试elastic search常用方法
 * @author: WhyWhatHow
 * @create: 2020-04-04 17:18
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ESTest {
    String mapping = " {\n" +
            " \t\"properties\": {\n" +
            "            \"studymodel\":{\n" +
            "             \"type\":\"keyword\"\n" +
            "           },\n" +
            "            \"name\":{\n" +
            "             \"type\":\"keyword\"\n" +
            "           },\n" +
            "           \"description\": {\n" +
            "              \"type\": \"text\",\n" +
            "              \"analyzer\":\"ik_max_word\",\n" +
            "              \"search_analyzer\":\"ik_smart\"\n" +
            "           },\n" +
            "           \"pic\":{\n" +
            "             \"type\":\"text\",\n" +
            "             \"index\":false\n" +
            "           }\n" +
            " \t}\n" +
            "}";

    // 测试添加索引库
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;
    private String doc;
    private String xcCourse;

    @Test
    public void testCreateIndexDB() throws IOException {
//         1 设置请求对象
        CreateIndexRequest request = new CreateIndexRequest("xc_course");
//         2 设置索引
        request.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));
//         3 设置映射
        request.mapping("doc", mapping, XContentType.JSON);
//         4  获取索引哭护短对象
        IndicesClient indices = client.indices();
//         5 获取response 对象
        CreateIndexResponse response = indices.create(request);
//         6 获取相应结果
        boolean acknowledged = response.isAcknowledged();
        System.out.println(acknowledged);
    }

    @Test
    public void testAddDoc() throws IOException {
//        1  获取添加数据对象
        Map<String, Object> map = new HashMap<>();
        map.put("name", "spring 实战");
        map.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        map.put("studymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("timestamp", dateFormat.format(new Date()));
        map.put("price", 5.6f);
//        2. 获取request
        IndexRequest request = new IndexRequest("xc_course", "doc");
//        3. 为请求方法添加数据对象
        request.source(map);
//        4. 获取返回参数
        IndexResponse response;
        response = client.index(request);
        DocWriteResponse.Result result = response.getResult();
        System.out.println(result);
    }
    @Test
    public  void  testSearchById() throws IOException {

        doc = "doc";
        xcCourse = "xc_course";

        GetRequest request = new GetRequest(xcCourse, doc,"6IfvRHEBHfDMpPxXQwgZ");
        GetResponse response = client.get(request);
        boolean exists = response.isExists();
        if (exists) {
            Map<String, Object> map = response.getSourceAsMap();
//            map.forEach();
            System.out.println(map);
        }

    }

    /**
     * 先删除源doc,然后在创建新doc
     * @throws IOException
     */
    @Test
    public  void testUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("xc_course","doc","6IfvRHEBHfDMpPxXQwgZ");
        HashMap<String, Object> map= new HashMap<>();
        map.put("name", "w哈哈哈");
        //  设置 数据对象
        request.doc(map);

        UpdateResponse response = client.update(request);
        DocWriteResponse.Result result = response.getResult();
        System.out.println(response);
        System.out.println(result);
    }
    @Test
    public  void testDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("xc_course","doc","3");
        //         request = new UpdateRequest("xc_course","doc","6IfvRHEBHfDMpPxXQwgZ");
//        HashMap<String, Object> map= new HashMap<>();
//        map.put("name", "w哈哈哈");
//        //  设置 数据对象
//        request.doc(map);

        DeleteResponse response = client.delete(request);
        DocWriteResponse.Result result = response.getResult();
        System.out.println(response);
        System.out.println(result);
    }
}
