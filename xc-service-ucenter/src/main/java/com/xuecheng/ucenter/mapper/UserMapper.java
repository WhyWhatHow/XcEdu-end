package com.xuecheng.ucenter.mapper;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

//    @Select("select xu.* companyid from xc_user xu , xc_company_user cu WHERE  xu.`id` = cu.`user_id` AND xu.username =#{username}")
    public XcUserExt getUserInfoByUsername(String username);
//    @Select("SELECT xm.* FROM xc_user_role xr ,xc_permission xp ,xc_menu xm \n" +
//            "\n" +
//            "WHERE \n" +
//            "xr.`user_id`=#{id}\n" +
//            "AND \n" +
//            "xr.`role_id`=xp.`role_id`\n" +
//            "AND xm.id=xp.`menu_id`\n")
    public List<XcMenu> getUserMenuByUid(String id);
}
