package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UserControllerApi;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-22 21:00
 **/
@RestController
@RequestMapping("/ucenter")
public class UserController implements UserControllerApi {
    @Autowired
    UserService service ;
    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserExt(String username) {
        return service.getUserExt(username);
    }
}
