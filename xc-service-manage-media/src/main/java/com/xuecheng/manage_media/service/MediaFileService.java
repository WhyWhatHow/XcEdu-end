package com.xuecheng.manage_media.service;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;

import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-18 15:27
 **/
@Service
public class MediaFileService {
    @Autowired
    MediaFileRepository repository;
    /**
     * query media Info
     * tag , fileOriginName --------like search
     * processStatus ----------total  search
     * @param page
     * @param size
     * @param request
     * @return
     */
    public QueryResponseResult queryList(int page, int size, QueryMediaFileRequest request) {
        if (request == null) {
            request = new QueryMediaFileRequest();
        }
        if (page < 1) {
            // 前端页码显示从1开始，后端页码从零开始
            page = 0;
        } else {
            page--;
        }
        if (size < 1) {
            size = 10;
        }
        MediaFile file = new MediaFile();
        Pageable pageable = PageRequest.of(page, size);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());

        if (StringUtils.isNotEmpty(request.getTag())) {
            file.setTag(request.getTag());
        }
        if (StringUtils.isNotEmpty(request.getFileOriginalName())) {
            file.setFileOriginalName(request.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(request.getProcessStatus())) {
            file.setProcessStatus(request.getProcessStatus());
        }
        Example<MediaFile> example = Example.of(file,exampleMatcher);
        Page<MediaFile> all = repository.findAll(example, pageable);
        //分页查询
        QueryResult<MediaFile> queryResult = new QueryResult<MediaFile>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

}
