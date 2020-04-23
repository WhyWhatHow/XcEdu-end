package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户中心" )
public interface UserControllerApi {

    @ApiOperation("获取用户信息")
    public XcUserExt getUserExt(String username);

//    @ApiOperation("获取用户信息")
//    public XcUser getUserInfo();

}
