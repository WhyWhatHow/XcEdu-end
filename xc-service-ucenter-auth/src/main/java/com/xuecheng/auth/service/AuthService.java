package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: XcEduCode
 * @description: 用户认证服务
 * @author: WhyWhatHow
 * @create: 2020-04-22 13:47
 **/
@Service
public class AuthService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecert;
    @Value("${auth.tokenValiditySeconds}")
    Integer tokenValiditySeconds;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    Integer cookieMaxAge;

    /**
     * 用户登录
     * 1. 校验登录信息
     * 2. 申请jwt令牌 (restTemplate 调用Spring Security)
     * 3. 将用户JWT保存到redis
     * wrong ,前端收不到4. 保存用户信息到cookie中,前端接受不到
     *
     * @param request
     * @return
     */
    public LoginResult login(LoginRequest request) {
        checkLoginInfo(request);

        AuthToken token = applyJWT(request.getUsername(), request.getPassword(), clientId, clientSecert);

        boolean res = saveTokenToRedis(token, tokenValiditySeconds);
        if (!res) {
            RuntimeExceptionCast.cast(AuthCode.SAVE_TOKEN_TO_REDIS_FAIL);
        }
//        saveTokenToCookie(token.getUser_token(),cookieMaxAge);
        return new LoginResult(CommonCode.SUCCESS, token.getUser_token());
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

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * save token to redis
     * 1.拼装 key
     * 2. 保存
     * 3,. 判断是否过期
     *
     * @param token
     * @param ttl
     */
    private boolean saveTokenToRedis(AuthToken token, Integer ttl) {

        String key = "user_token:" + token.getUser_token();
        String content = JSON.toJSONString(token);
        redisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 申请令牌
     * 1. 获取 uri
     * 2. http header ,body 拼装
     * 3. 发送 spring security 校验
     * 4. 校验map,返回authToken
     *
     * @param username     用户名
     * @param password     密码
     * @param clientId     http header basic
     * @param clientSecert http header basic
     * @return authToken: access_token ,refresh_token, user_token(jti)
     */
    private AuthToken applyJWT(String username, String password, String clientId, String clientSecert) {
        //1 . get uri
        ServiceInstance choose = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = choose.getUri();
        if (uri == null) {
            RuntimeExceptionCast.cast(AuthCode.URI_FAIL);
        }
        String url = uri + "/auth/oauth/token";
        // 2 set header
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String base64 = getHttpBasicAuth(clientId, clientSecert);
        headers.add("Authorization", base64);
//        // 3 set body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.set("grant_type", "password");
        body.set("username", username);
        body.set("password", password);
        HttpEntity entity = new HttpEntity(body, headers);
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
//                if (clientHttpResponse.getRawStatusCode() != 400 && clientHttpResponse.getRawStatusCode() != 401) {
//                    super.handleError(clientHttpResponse);
//                }
            }
        });
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map map = response.getBody();
        return checkAndSaveToken(map);
    }

    /**
     * 处理response body,拼装authToken
     * 处理异常错误 account not exist, password or usename is wrong
     * 1. 处理账号不存在
     * 2. 处理账号密码错误
     * 3. 生成token并返回
     *
     * @param map
     * @return
     */
    private AuthToken checkAndSaveToken(Map map) {
        // account does not exist
        if (map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null) {
            String errMsg = (String) map.get("error_description");
            if (errMsg != null) {
                RuntimeExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
            } else {
                RuntimeExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }
        }
        String accessToken = (String) map.get("access_token");
        String refreshToken = (String) map.get("refresh_token");
        String userToken = (String) map.get("jti");
        return new AuthToken(accessToken, refreshToken, userToken);
    }

    /**
     * 设值 http header bassic 认证
     *
     * @param clientId
     * @param clientSecert
     * @return
     */
    private String getHttpBasicAuth(String clientId, String clientSecert) {
        String s = clientId + ":" + clientSecert;
        byte[] encode = Base64.getEncoder().encode(s.getBytes());
        String out = new String(encode);
        return "Basic " + out;
    }

    /**
     * 校验用户登录信息,
     *
     * @param request
     * @return
     */
    private boolean checkLoginInfo(LoginRequest request) {
        if (request == null) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        } else {
            if (StringUtils.isEmpty(request.getUsername())) {
                RuntimeExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
            }
            if (StringUtils.isEmpty(request.getPassword())) {
                RuntimeExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
            }
//             // TODO: 2020/4/22 将 验证码添加
//            if(StringUtils.isEmpty(request.getVerifycode())){
//                RuntimeExceptionCast.cast(AuthCode.AUTH_VERIFYCODE_NONE);
//            }
        }
        return true;
    }

    /**
     * 根据usertoken 获取accesstoken
     *
     * @param user_token
     * @return
     */
    public JwtResult getJWTByUserToken(String user_token) {
        String userToken = "user_token:" + user_token;
        String jwt = redisTemplate.opsForValue().get(userToken);
        if (StringUtils.isEmpty(jwt)) {
            return new JwtResult(CommonCode.FAIL, null);
        }
        return new JwtResult(CommonCode.SUCCESS, jwt);
    }

    /**
     * 退出当前用户
     * redis 当前用户信息
     * 清空cookie
     *
     * @return
     */
    public ResponseResult logOut(String token) {
        String userToken = "user_token:" + token;
        redisTemplate.delete(userToken);
        this.saveTokenToCookie(token, 0);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
