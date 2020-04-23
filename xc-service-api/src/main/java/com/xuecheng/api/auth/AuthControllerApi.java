package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户认证接口")
public interface AuthControllerApi {
    @ApiOperation("用户登录")
    public LoginResult login(LoginRequest request);
    @ApiOperation("用户退出")
    public ResponseResult logOut();
    @ApiOperation("通过cookie获取用户AccessToken信息")
    public JwtResult getUserAccessTokenByCookie();

}
