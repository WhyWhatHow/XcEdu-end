package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageRemotePostResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.TimeUtil;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @program: XcEduCode
 * @description: 课程发布service层
 * @author: WhyWhatHow
 * @create: 2020-04-19 15:39
 **/
@Service
public class CoursePublishService {
    // 从apllication.yml中获取配置信息
    @Value("${course-publish.siteId}")
    String publish_siteId;
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    @Autowired
    CourseService service;


    @Autowired
    CmsPageClient pageClient;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarketRepository marketRepository;

    @Autowired
    CoursePubRepository coursePubRepository;

    /**
     * 根据课程id 获取页面预览的url
     * step:
     * 1. 组成cmspage页面
     * 2, 调用远程方法保存cmsPage
     * 3. 返回页面预览url
     * @param courseId
     * @return 页面详情页预览的url
     */
    public CoursePreviewResult previewCourseDetail(String courseId) {
        CourseBase base = service.findCourseBaseByCourseId(courseId);
        CmsPage page = initCmsPage(base, courseId);
        CmsPageResult result = pageClient.savePage(page);
        if (!result.isSuccess()) {
            RuntimeExceptionCast.cast(CourseCode.COURSE_PUBLISH_VIEWERROR);
        }
        CmsPage downloadPage = result.getCmsPage();
        String url = previewUrl + downloadPage.getPageId();
        return new CoursePreviewResult(CommonCode.SUCCESS, url);
    }

    /**
     * 发布课程
     * 1, 准备cmspage 数据
     * 2. 调用cmspage.发布功能
     * 3. 修改页面信息,
     * 4. 向 elastic search 添加索引
     *      course_pub : 课程页面查询使用
     *      course_media_pub: 课程媒资信息锁芯
     * 5. 返回结果
     *
     * @param courseId
     * @return
     */
    @Transactional
    public CoursePublishResult publishCourseDetail(String courseId) {
        CourseBase base = service.findCourseBaseByCourseId(courseId);
        if (base == null) {
            // 排除课程不存在的情况
            RuntimeExceptionCast.cast(CommonCode.FAIL);
        }
//         初始化请求cmsPage页面
        CmsPage page = initCmsPage(base, courseId);
        // 发送请求
        CmsPageRemotePostResult result = pageClient.postPage(page);
        if (!result.isSuccess()) {
            RuntimeExceptionCast.cast(CommonCode.FAIL);
        }
        String url = result.getUrl();
        // 跟新页面信息
        base.setStatus("202002");
        courseBaseRepository.save(base);
        //// Done: 2020/3/29   将 课程信息 加入到缓存中,索引中
        CoursePub pub = saveCoursePub(base);
        saveCourseMediaByCourseId(courseId);

        return new CoursePublishResult(CommonCode.SUCCESS, url);
    }

    /**
     * 初始化cmsPage页面
     *
     * @param courseId
     * @return
     */
    private CmsPage initCmsPage(CourseBase base, String courseId) {
        // 组成cmsPage页面信息 pageId: 4028e581617f945f01617f9dabc40000
        //                            4028e581617f945f01617f9dabc40000
        CmsPage page = new CmsPage();
        page.setPageName(courseId + ".html");
        page.setPageAliase(base.getName());
        //  siteId: 5e800c8b83e280056463e674 # 站点id
        page.setSiteId(publish_siteId);
        //  templateId: 5e7c64c99484362aa8210030 # 课程详情页面模板id
        page.setTemplateId(publish_templateId);
        //  pageWebPath: /course/detail/
        page.setPageWebPath(publish_page_webpath);
        //  pagePhysicalPath: /course/detail/
        page.setPagePhysicalPath(publish_page_physicalpath);
        //  dataUrlPre: http://localhost:31200/course/courseview/
        page.setDataUrl(publish_dataUrlPre + courseId);
        //  previewUrl: http://www.xuecheng.com/cms/preview/
        return page;
    }

    /**
     * 1.  保存课程基本信息, 课程图片,课程教学计划, 课程市场计划到coursePub中
     * @param base
     * @return
     */
    private CoursePub saveCoursePub(CourseBase base) {
        CoursePub pub = new CoursePub();
        String id = base.getId();
        //1  课程计划
        Optional<CourseMarket> opt1 = marketRepository.findById(base.getId());
        if (opt1.isPresent()) {
            CourseMarket courseMarket = opt1.get();
            BeanUtils.copyProperties(courseMarket, pub);
        }
        //2  处理课程教学计划
        TeachplanNode teachPlanNode = service.getTeachPlanByCourseId(id);
//        BeanUtils.copyProperties(teachPlanNode,pub);
        if (teachPlanNode != null) {
            String teachplan = JSON.toJSONString(teachPlanNode);
            pub.setTeachplan(teachplan);
        }
        CoursePic coursePic = service.findCoursePicByCourseId(id);
        if (coursePic != null) {
            BeanUtils.copyProperties(coursePic, pub);
        }
        //  4 处理课程基本信息
        BeanUtils.copyProperties(base, pub);
        pub.setTimestamp(new Date()); // 用来让logstash更新数据
        String date = TimeUtil.getNow();
        pub.setPubTime(date);
        CoursePub save = coursePubRepository.save(pub);
        return save;
    }
    @Autowired
    TeachPlanMediaPubRepository teachPlanMediaPubRepository;
    @Autowired
    TeachPlanMediaRepository teachPlanMediaRepository;
    /**
     * 根据课程id 保存课程的媒资信息, course :teachplan = 1:n
     * teachplan_meida : save 查询
     * teachplan_meida_pub: show   delete -> save
     * @param courseId 课程id
     */
    private void saveCourseMediaByCourseId(String courseId) {
        List<TeachplanMedia> list = teachPlanMediaRepository.findByCourseId(courseId);
        if (list == null || list.size() == 0) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // delete media_pub already exist
        teachPlanMediaPubRepository.deleteByCourseId(courseId);
        // add data to media_pub
        ArrayList<TeachplanMediaPub> pubArrayList = new ArrayList<>();
        for (TeachplanMedia media : list) {
            TeachplanMediaPub pub = new TeachplanMediaPub();
            BeanUtils.copyProperties(media, pub);
            pub.setTimestamp(new Date());
            System.out.println(pub);
            pubArrayList.add(pub);
        }
        teachPlanMediaPubRepository.saveAll(pubArrayList);
    }

}
