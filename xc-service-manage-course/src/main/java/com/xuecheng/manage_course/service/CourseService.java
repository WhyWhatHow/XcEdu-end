package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.controller.CourseController;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.mapper.TeachPlanMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 设置节点的等级 ,TODO 貌似没有任何卵用, reason: 目前为止尚未用到过grade
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
            marketRepository.save(one);
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
}
