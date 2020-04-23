package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-22 13:45
 **/
@RestController
public class AuthController implements AuthControllerApi {
    @Autowired
    AuthService service;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    Integer cookieMaxAge;


    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest request) {
        LoginResult login = service.login(request);
        saveTokenToCookie(login.getToken(), cookieMaxAge);
        return login;
    }

    @Override
    @GetMapping("/userlogout")
    public ResponseResult logOut() {
        String user_token = getTokenFromCookie("uid");
        return service.logOut(user_token);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult getUserAccessTokenByCookie() {
        String user_token = getTokenFromCookie("uid");
        return service.getJWTByUserToken(user_token);
    }


    /**
     * 通过关键字从cookie获取value
     *
     * @param key
     * @return token
     */
    private String getTokenFromCookie(String key) {
        if (StringUtils.isEmpty(key)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, key);
        String s = map.get(key);
        return s;
    }

    /**
     * 将 user_token保存到cookie中
     *
     * @param token
     * @param cookieMaxAge
     */
    private void saveTokenToCookie(String token, Integer cookieMaxAge) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }

}
