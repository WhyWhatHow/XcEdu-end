package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @program: XcEduCode
 * @description: 系统配置信息接口
 * @author: WhyWhatHow
 * @create: 2020-03-22 14:05
 **/
@Api("数据字典接口")
public interface SysDictionaryControllerApi {
@ApiOperation("根据类型获取字典信息")
    public SysDictionary findByDtype(String type);
}
