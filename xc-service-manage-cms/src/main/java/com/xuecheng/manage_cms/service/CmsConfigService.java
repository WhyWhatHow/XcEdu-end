package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigService {
    @Autowired
    CmsConfigRepository repository;

    public CmsConfig findById(String id) {
        Optional<CmsConfig> optional = repository.findById(id);
        if (optional.isPresent()) {
            return  optional.get();
        }
        return null;
    }
}