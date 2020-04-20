package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.response.LearnCode;
import com.xuecheng.framework.domain.learning.response.MediaUrlResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.ESClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-19 20:45
 **/
@Service
public class CourseLearnService {
    /**
     * 获取媒资url
     * 1. 校验学生权限
     * 2. feign 获取媒资信息
     *
     * @param courseId 课程id
     * @param id 教案id
     * @return
     */
    @Autowired
    ESClient client;

    public MediaUrlResult getMediaUrl(String courseId, String teachplanId) {
//         todo  校验 用户权限 ,
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(teachplanId)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        TeachplanMediaPub teachplanMediaPub = client.findMeidaByTpId(teachplanId);
        String url = teachplanMediaPub.getMediaUrl();
        if (StringUtils.isEmpty(url)) {
            RuntimeExceptionCast.cast(LearnCode.GET_MEDIA_URL_FALSE);
        }
        return new MediaUrlResult(CommonCode.SUCCESS, url);

    }
}
