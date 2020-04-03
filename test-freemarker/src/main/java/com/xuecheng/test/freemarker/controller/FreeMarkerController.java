package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
// 静态化不可以使用 RestController注解,原因: freemarker需要把返字符串,对应 resources/templates/xxx
@RequestMapping("/freemarker")
public class FreeMarkerController {

    @Autowired
    RestTemplate restTemplate ; // 处理restTemplate, web客户端请求

    @GetMapping("/banner")
    String getBanner(Map map){
        ResponseEntity<Map> en = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = en.getBody();
        map.putAll(body);
        return "index_banner";
    }
    @GetMapping("/coursedetail")
    String getCourseDetail(Map map){
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://127.0.0.1:31200/course/courseview/4028e581617f945f01617f9dabc40000", Map.class);
        Map body = entity.getBody();
        System.out.println(body);
        map.putAll(body);
        return "course";

    }
    @GetMapping("/test01")
    public  String getPage(Map<String, Object> map){
        //向数据模型放数据
        map.put("name","黑马程序员");
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMondy(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMondy(200.1f);
        stu2.setAge(19);
//        stu2.setBirthday(new Date());
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus",stus);
        //准备map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //向数据模型放数据
        map.put("stu1",stu1);
        //向数据模型放数据
        map.put("stuMap",stuMap);
        //返回模板文件名称
        map.put("name", "shelldon");
        return  "test01";
    }
}
