package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import com.xuecheng.ucenter.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-22 21:08
 **/
@Service
public class UserService {
    @Autowired
    XcUserRepository userRepository;
    @Autowired
    XcCompanyUserRepository companyRepository;
//    @Autowired
//    UserMapper userMapper;

    /**
     * 根据username 查询userExt信息
     * 1 获取用户的基本信息+companyId
     * 2 获取用户权限
     *
     * @param username
     * @return
     */
    public XcUserExt getUserExt(String username) {
        if (StringUtils.isEmpty(username)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //// TODO: 2020/4/22  ======================
        XcUser xcUser = userRepository.findByUsername(username);
        if (xcUser == null || StringUtils.isEmpty(xcUser.getId())) {
            RuntimeExceptionCast.cast(UcenterCode.USERINFO_GET_FAIL);
        }

        XcUserExt ext =new XcUserExt();
        String id = xcUser.getId();
        BeanUtils.copyProperties(xcUser,ext);
        XcCompanyUser company = companyRepository.findByUserId(id);
        ext.setCompanyId(company.getCompanyId());
        return ext ;
        //// TODO: 2020/4/22 利用mappe处理
//        1. 获取用户基本信息
//        XcUserExt userExt = userMapper.getUserInfoByUsername(username);
//        if(userExt==null|| StringUtils.isEmpty(userExt.getId())){
//            RuntimeExceptionCast.cast(UcenterCode.USERINFO_GET_FAIL);
//        }
//        String userID = userExt.getId();
////        2 获取用户权限信息
//        List<XcMenu> list =  userMapper.getUserMenuByUid(userID);
//        userExt.setPermissions(list);
//        return userExt;
    }
}
