package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-03-22 14:12
 **/
@Service
public class SysDictionaryService {

    @Autowired
    SysDictionaryRepository repository;

    public SysDictionary findByType(String type) {
        if (StringUtils.isEmpty(type)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        return repository.findByDType(type);

    }
}
