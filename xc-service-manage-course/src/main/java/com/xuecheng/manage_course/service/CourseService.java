package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageRemotePostResult;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.response.CoursePreviewResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseDetail;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.TimeUtil;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.mapper.TeachPlanMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @program: XcEduCode
 * @description: 课程管理的service
 * @author: WhyWhatHow
 * @create: 2020-03-22 12:22
 **/
@Service
public class CourseService {
    @Autowired
    TeachPlanMapper teachPlanMapper;

    public TeachplanNode getTeachPlanByCourseId(String id) {
        if (StringUtils.isEmpty(id)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        TeachplanNode node = teachPlanMapper.selectTeachPlanByCourseId(id);
        return node;
    }

    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String parentid = teachplan.getParentid();
        String courseid = teachplan.getCourseid();
        // parentID 为空,代表上级节点未选中, 即用户创建的课程计划为本课程的第一级别的课程计划
        if (parentid == null) {
            //处理一级节点,以及第一次创建课程计划时的情况
            parentid = findTeachPlanIdInCourseByCourseId(courseid);
        }
        teachplan.setParentid(parentid);
        // 设置节点的等级
        Optional<Teachplan> optional = teachPlanRepository.findById(parentid);
        if (!optional.isPresent()) {
            RuntimeExceptionCast.cast(CourseCode.COURSE_TEACHPLAN_ROOT_IS_NULL);
        }
        Teachplan root = optional.get();
        String grade = root.getGrade();
        if (grade.equals("1")) {
            teachplan.setGrade("2");
        } else {
            teachplan.setGrade("3");
        }
        Teachplan save = teachPlanRepository.save(teachplan);
        if (save != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    @Autowired
    TeachPlanRepository teachPlanRepository;

    /**
     * 根据课程ID查询课程教学计划
     *
     * @param courseid
     * @return
     */
    String findTeachPlanIdInCourseByCourseId(String courseid) {
        Optional<CourseBase> opt = courseBaseRepository.findById(courseid);
        if (!opt.isPresent()) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CourseBase courseBase = opt.get();
        List<Teachplan> list = teachPlanRepository.findByCourseidAndParentid(courseid, "0");
        if (list == null || list.size() == 0) {
            // 当前课程尚未添加教学计划
            //新增一个根结点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseid);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");//1级
            teachplanRoot.setStatus("0");//未发布
            teachPlanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = list.get(0);
        return teachplan.getId();
    }

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        if (StringUtils.isEmpty(courseBase.getName()) || StringUtils.isEmpty(courseBase.getGrade()) || StringUtils.isEmpty(courseBase.getStudymodel())) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    @Autowired
    CourseMapper mapper;

    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
//        Page<CourseInfo> page1 = Page.
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        if (page < 1) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> infoPage = mapper.findCourseList(courseListRequest);
        List<CourseInfo> list = infoPage.getResult();
        long total = infoPage.getTotal();
        QueryResult result = new QueryResult();
        result.setList(list);
        result.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, result);
//        PageHelper.startPage()
    }

    public CourseBase findCourseBaseByCourseId(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            RuntimeExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseBase> opt = courseBaseRepository.findById(courseId);
        if (StringUtils.isNotEmpty(courseId) && opt.isPresent()) {
            CourseBase courseBase = opt.get();
            return courseBase;
        }
        return null;
    }

    @Transactional
    public ResponseResult updateCourseBaseInfo(String id, CourseBase courseBase) {
        CourseBase base = this.findCourseBaseByCourseId(id);
        if (base == null) {
            RuntimeExceptionCast.cast(CourseCode.COURSE_BASE_ISNULL);
        }
        base.setName(courseBase.getName());
        base.setMt(courseBase.getMt());
        base.setSt(courseBase.getSt());
        base.setGrade(courseBase.getGrade());
        base.setStudymodel(courseBase.getStudymodel());
        base.setUsers(courseBase.getUsers());
        base.setDescription(courseBase.getDescription());
        CourseBase save = courseBaseRepository.save(base);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Autowired
    CourseMarketRepository marketRepository;

    public CourseMarket findmarketByCourseId(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            RuntimeExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseMarket> opt = marketRepository.findById(courseId);
        if (StringUtils.isNotEmpty(courseId) && opt.isPresent()) {
            CourseMarket courseBase = opt.get();
            return courseBase;
        }
        return null;
    }

    @Transactional
    public CourseMarket updateCourseMarketInfo(String id, CourseMarket market) {
        CourseMarket one = this.findmarketByCourseId(id);
        if (one != null) {
            one.setCharge(market.getCharge());
            one.setStartTime(market.getStartTime());//课程有效期，开始时间
            one.setEndTime(market.getEndTime());//课程有效期，结束时间
            one.setPrice(market.getPrice());
            one.setQq(market.getQq());
            one.setValid(market.getValid());
//            marketRepository.save(one);
        } else {
            one = new CourseMarket();
            BeanUtils.copyProperties(market, one);
        }
        one.setId(id);
        marketRepository.save(one);

        // 如下写法:若修改失败,则无反馈结果,所以进行修改
//        return new ResponseResult(CommonCode.SUCCESS);
        return one;
    }

    @Autowired
    CoursePictureRepository pictureRepository;

    @Transactional
    public ResponseResult addCoursePic(String courseId, String fileId) {
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(fileId)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CoursePic coursePic = new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(fileId);
        CoursePic save = pictureRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePicByCourseId(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CoursePic> optional = pictureRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Transactional
    public ResponseResult deleteCoursePicById(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        long res = pictureRepository.deleteCoursePicByCourseid(courseId);
        if (res > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    public CourseDetail getCourseDetailById(String courseId) {
        CourseMarket market = this.findmarketByCourseId(courseId);
        CourseBase courseBase = this.findCourseBaseByCourseId(courseId);
        TeachplanNode node = this.getTeachPlanByCourseId(courseId);
        CoursePic pic = this.findCoursePicByCourseId(courseId);
        return new CourseDetail(courseBase, market, pic, node);
    }

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

    /**
     * 根据课程id 获取页面预览的url
     * step:
     * 1. 组成cmspage页面
     * 2, 调用远程方法保存cmsPage
     * 3. 返回页面预览url
     * @param courseId
     * @return 页面详情页预览的url
     */
//    public CoursePreviewResult previewCourseDetail(String courseId) {
//        CourseBase base = findCourseBaseByCourseId(courseId);
//        CmsPage page = initCmsPage(base, courseId);
//        CmsPageResult result = pageClient.savePage(page);
//        if (!result.isSuccess()) {
//            RuntimeExceptionCast.cast(CourseCode.COURSE_PUBLISH_VIEWERROR);
//        }
//        CmsPage downloadPage = result.getCmsPage();
//        String url = previewUrl + downloadPage.getPageId();
//        return new CoursePreviewResult(CommonCode.SUCCESS, url);
//    }

    /**
     * 初始化cmsPage页面
     *
     * @param courseId
     * @return
     */
//    private CmsPage initCmsPage(CourseBase base, String courseId) {
//        // 组成cmsPage页面信息 pageId: 4028e581617f945f01617f9dabc40000
//        //                            4028e581617f945f01617f9dabc40000
//        CmsPage page = new CmsPage();
//        page.setPageName(courseId + ".html");
//        page.setPageAliase(base.getName());
//        //  siteId: 5e800c8b83e280056463e674 # 站点id
//        page.setSiteId(publish_siteId);
//        //  templateId: 5e7c64c99484362aa8210030 # 课程详情页面模板id
//        page.setTemplateId(publish_templateId);
//        //  pageWebPath: /course/detail/
//        page.setPageWebPath(publish_page_webpath);
//        //  pagePhysicalPath: /course/detail/
//        page.setPagePhysicalPath(publish_page_physicalpath);
//        //  dataUrlPre: http://localhost:31200/course/courseview/
//        page.setDataUrl(publish_dataUrlPre + courseId);
//        //  previewUrl: http://www.xuecheng.com/cms/preview/
//        return page;
//    }

    @Autowired
    CmsPageClient pageClient;

    @Autowired
    CoursePubRepository coursePubRepository;

    /**
     * 发布课程
     * 1, 准备cmspage 数据
     * 2. 调用cmspage.发布功能
     * 3. 修改页面信息,
     * 4. 向 elastic search 添加索引
     * course_pub : 课程页面查询使用
     * course_media_pub: 课程媒资信息锁芯
     * 5. 返回结果
     *
     * @param courseId
     * @return
     */
//    @Transactional
//    public CoursePublishResult publishCourseDetail(String courseId) {
//        CourseBase base = findCourseBaseByCourseId(courseId);
//        if (base == null) {
//            // 排除课程不存在的情况
//            RuntimeExceptionCast.cast(CommonCode.FAIL);
//        }
////         初始化请求cmsPage页面
//        CmsPage page = initCmsPage(base, courseId);
//        // 发送请求
//        CmsPageRemotePostResult result = pageClient.postPage(page);
//        if (!result.isSuccess()) {
//            RuntimeExceptionCast.cast(CommonCode.FAIL);
//        }
//        String url = result.getUrl();
//        // 跟新页面信息
//        base.setStatus("202002");
//        courseBaseRepository.save(base);
//        //// Done: 2020/3/29   将 课程信息 加入到缓存中,索引中
//        CoursePub pub = saveCoursePub(base);
//        // 保存课程的媒资信息,并加入es 索引库中
//        saveCourseMediaByCourseId(courseId);
//
//        return new CoursePublishResult(CommonCode.SUCCESS, url);
//    }

    @Autowired
    TeachPlanMediaPubRepository teachPlanMediaPubRepository;

    /**
     * 根据课程id 保存课程的媒资信息, course :teachplan = 1:n
     * teachplan_meida : save 查询
     * teachplan_meida_pub: show   delete -> save
     * @param courseId 课程id
     */
//    private void saveCourseMediaByCourseId(String courseId) {
//        List<TeachplanMedia> list = teachPlanMediaRepository.findByCourseId(courseId);
//        if (list == null || list.size() == 0) {
//            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
//        }
//        // delete media_pub already exist
//        long l = teachPlanMediaPubRepository.deleteByCourseId(courseId);
//        if (l == 0) {
//            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
//        }
//        for (TeachplanMedia media : list) {
//            TeachplanMediaPub pub = new TeachplanMediaPub();
//            BeanUtils.copyProperties(media, pub);
//            pub.setTimestamp(new Date());
//            System.out.println(pub);
//            teachPlanMediaPubRepository.save(pub);
//        }
//    }

    /**
     * 1.  保存课程基本信息, 课程图片,课程教学计划, 课程市场计划到coursePub中
     * @param base
     * @return
     */
//    private CoursePub saveCoursePub(CourseBase base) {
//        CoursePub pub = new CoursePub();
//        String id = base.getId();
//        //1  课程计划
//        Optional<CourseMarket> opt1 = marketRepository.findById(base.getId());
//        if (opt1.isPresent()) {
//            CourseMarket courseMarket = opt1.get();
//            BeanUtils.copyProperties(courseMarket, pub);
//        }
//        //2  处理课程教学计划
//        TeachplanNode teachPlanNode = this.getTeachPlanByCourseId(id);
////        BeanUtils.copyProperties(teachPlanNode,pub);
//        if (teachPlanNode != null) {
//            String teachplan = JSON.toJSONString(teachPlanNode);
//            pub.setTeachplan(teachplan);
//        }
//        CoursePic coursePic = findCoursePicByCourseId(id);
//        if (coursePic != null) {
//            BeanUtils.copyProperties(coursePic, pub);
//        }
//        //  4 处理课程基本信息
//        BeanUtils.copyProperties(base, pub);
//        pub.setTimestamp(new Date()); // 用来让logstash更新数据
//        String date = TimeUtil.getNow();
//        pub.setPubTime(date);
////        System.out.println(pub);
//        CoursePub save = coursePubRepository.save(pub);
//        return save;
//    }


    @Autowired
    TeachPlanMediaRepository teachPlanMediaRepository;

    /**
     * 保存媒资信息  只有三级节点可以保存视频信息,其他节点不可以保存
     * 1. 判断是否是三级节点
     * 2 . 写入mysql,先查,有文档,则更新,无则插入
     *
     * @param teachplanMedia
     * @return
     */
    @Transactional
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null) {
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //1  判断是否是三级节点
        Teachplan byIdAndGrade = teachPlanRepository.findByIdAndGrade(teachplanMedia.getTeachplanId(), "3");
        if (byIdAndGrade == null) {
            // 不存在
            RuntimeExceptionCast.cast(CourseCode.COURSE_MEDIS_GRADE_ERROR);
        }
        // save mysql 中
        Optional<TeachplanMedia> opt = teachPlanMediaRepository.findById(teachplanMedia.getTeachplanId());
        if (opt.isPresent()) {
            //  exist
            TeachplanMedia before = opt.get();
            BeanUtils.copyProperties(teachplanMedia, before);
            TeachplanMedia save = teachPlanMediaRepository.save(before);
        } else {
            teachPlanMediaRepository.save(teachplanMedia);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
