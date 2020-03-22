package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-22 14:07
 **/
@RestController
@RequestMapping("/sys/dictionary")
public class SYSDictionaryController implements SysDictionaryControllerApi {
    @Autowired
    SysDictionaryService service;
    @Override
    @GetMapping("/get/{type}")
    public SysDictionary findByDtype(@PathVariable("type") String type) {
     return  service.findByType(type);}
}
