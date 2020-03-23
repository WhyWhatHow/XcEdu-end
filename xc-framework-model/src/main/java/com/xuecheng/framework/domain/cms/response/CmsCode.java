package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

/**
 * Created by mrt on 2018/3/5.
 */
@ToString
public enum CmsCode implements ResultCode {
    CMS_ADDPAGE_EXISTSNAME(false,24001,"页面名称已存在！"),
    // gengerator html
    CMS_GENERATEHTML_DATAURLISNULL(false,24002,"从页面信息中找不到获取数据的url！"),
    CMS_GENERATEHTML_DATAISNULL(false,24003,"根据页面的数据url获取不到数据！"),
    CMS_GENERATEHTML_TEMPLATEISNULL(false,24004,"页面模板为空！"),
    CMS_GENERATEHTML_HTMLISNULL(false,24005,"生成的静态html为空！"),
    CMS_GENERATEHTML_SAVEHTMLERROR(false,24005,"保存静态html出错！"),
    // Grid FS error
    CMS_GRIDFS_STORE_HTML_FAIL(false,241000,"存储HTML页面到gridfs中失败"),
    CMS_GRIDFS_STORE_FTL_FAIL(false,241001,"存储freemarker静态页面到gridfs中失败"),
    CMS_GRIDFS_READ_FILE_FAIL(false,241002,"读取gridfs文件中失败"),
    // postPage
    CMS_POSTPAGE_PAGEISNULL(false,242000,"发布页面, 页面无效"),
    CMS_POSTPAGE_WEBPATHISNULL(false,242001,"页面web路径为空"),
    CMS_POSTPAGE_PHYSICALPATHISNULL(false,242002,"页面物理路径为空"),
    CMS_POSTPAGE_HTMLFILEISNOTEXIST(false,242003,"下载文件不存在"),

    // course error
    CMS_COURSE_PERVIEWISNULL(false,24007,"预览页面为空！"),

    CMS_UNLEGAL_PARAMSINPURL(false,24008,"请求参数非法");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private CmsCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
