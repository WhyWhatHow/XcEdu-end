package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-07 14:56
 **/
@Service
public class EsSearchService {
    @Value("${xuecheng.elasticsearch.course.index}")
    private String index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field; // 数据源字段

    @Value("${xuecheng.elasticsearch.media.index}")
    private String meidaIndex;
    @Value("${xuecheng.elasticsearch.media.type}")
    private String mediaType;
    @Value("${xuecheng.elasticsearch.media.source_field}")
    private String mediaSourceField; // 数据源字段


    private int from = 0; // es 分页开始
    private int size = 10;// es 页面大小
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    /**
     * 实现分类搜索
     * 按关键字搜索  匹配课程名称, 课程内容 multiQuery
     * 实现分页
     * 实现按难度等级搜索  termQuery
     * 结果高亮显示
     * 1. 设置 from and size
     * 2. 设置查询条件
     *
     * @param page  起始页面
     * @param size  页面大小
     * @param param 查询参数
     * @return
     */
    public QueryResponseResult searchCourse(int page, int size, CourseSearchParam param) {

        initFromAndSize(page, size);
        if (param == null) {
            param = new CourseSearchParam();
        }
        SearchRequest request = getSearchRequest(index, type);
        // 1 设置查询条件,过滤条件,高亮
        SearchSourceBuilder builder = setSearchSourceBuilder(param);
        // 2 设置分页
        builder.from(this.from);
        builder.size(this.size);
        // 3 查询
        request.source(builder);
        SearchResponse response = null;
        try {
            response = client.search(request);
        } catch (IOException e) {
//            System.err.println(e.getStackTrace());
            RuntimeExceptionCast.cast(CourseCode.COURSE_SEARCH_ERROR);
        }
        // 4 处理查询结果集
        SearchHits hits = response.getHits();
        QueryResult queryResult = dealWithCourseHits(hits);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 设置查询条件,并返回 searchSourceBuilder
     *
     * @param param : 请求参数
     * @return
     */
    private SearchSourceBuilder setSearchSourceBuilder(CourseSearchParam param) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //0 设置搜索源字段
        String array[] = getSearchSourceField(source_field);
        builder.fetchSource(array, new String[]{});
        //1  多字段检索
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(param.getKeyword(), "name", "description", "teachplan").minimumShouldMatch("70%").field("name", 100);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //2  按照分类过滤
        if (StringUtils.isNotEmpty(param.getMt())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", param.getMt()));
        }
        if (StringUtils.isNotEmpty(param.getSt())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", param.getSt()));
        }
        // 3 按照难度等级查询
        if (StringUtils.isNotEmpty(param.getGrade())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", param.getGrade()));
        }
        //3.5 bind to searchSourceBuilder
        builder.query(boolQueryBuilder);
        // 4 结果高亮
        HighlightBuilder highlightBuilder = getHighlightBuilder();
        builder.highlighter(highlightBuilder);
        return builder;
    }

    // 处理搜索结果集 , 课程搜索结果集，只需要显示课程名称，课程价格，课程图片，课程描述
    private QueryResult dealWithCourseHits(SearchHits hits) {
        QueryResult<CoursePub> result = new QueryResult<>();
        SearchHit[] hits1 = hits.getHits();
        List<CoursePub> list = new ArrayList<>();
        for (SearchHit hit : hits1) {
            CoursePub pub = new CoursePub();
            Map<String, Object> map = hit.getSourceAsMap();
            //处理高亮字段
            //   id,
            pub.setId(hit.getId());
            //   name,
            String name = dealWithHighLightPart(hit.getHighlightFields());
            if (StringUtils.isEmpty(name)) {
                name = (String) map.get("name");
            }
            pub.setName(name);
            //   pic,
            String pic = (String) map.get("pic");
            pub.setPic(pic);
            // grade
            String grade = (String) map.get("grade");
            pub.setGrade(grade);
            // mt 大分类
            String mt = (String) map.get("mt");
            pub.setMt(mt);
            // st 小分类
            String st = (String) map.get("st");
            pub.setSt(st);
            // charge // 收费规则
            String charge = (String) map.get("charge");
            pub.setCharge(charge);
            // price
            Double price = (Double) map.get("price");
            Double price_old = (Double) map.get("price_old");
            pub.setPrice(price);
            pub.setPrice_old(price_old);
            //将pub对象放入list
            list.add(pub);
        }
        result.setTotal(hits.getTotalHits());
        result.setList(list);
        return result;
    }

    // 处理高亮字段
    private String dealWithHighLightPart(Map<String, HighlightField> highlightFields) {
        if (highlightFields == null) {
            // TODO: 2020/4/7
            RuntimeExceptionCast.cast(CommonCode.FAIL);
        }
        HighlightField highlightField = highlightFields.get("name");
        if (highlightField == null) {
            return "";
        }
//        StringBuilder not safety
        StringBuffer buffer = new StringBuffer();
        Text[] fragments = highlightField.getFragments();
        for (Text text : fragments) {
            buffer.append(text.string());
        }
        return buffer.toString();
    }

    // 获取显示数据源字段
    private String[] getSearchSourceField(String source_field) {
        if (StringUtils.isEmpty(source_field)) {
            return new String[]{};
        }
        String[] split = source_field.split(",");
        return split;
    }

    /**
     * 设置高亮
     *
     * @return
     */
    private HighlightBuilder getHighlightBuilder() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        ;
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
//        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        return highlightBuilder;
    }

    private void initFromAndSize(int page, int size) {
        if (page < 1) {
            this.from = 0;
            if (size < 0) {
                this.size = 10;
            }
        } else {
            this.from = (page - 1) * size;
            this.size = size;
        }
    }

    /**
     * 根据课程id 查询课程发布信息
     *
     * @param id
     * @return
     */
    public Map<String, CoursePub> getAll(String id) {
        // 获取searchrequest. 并配置基本项
        SearchRequest request = getSearchRequest(index, type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("id", id));
        request.source(builder);
        Map<String, CoursePub> resMap = new HashMap<>();
        try {
            SearchResponse response = client.search(request);
            SearchHits hits = response.getHits();
            SearchHit[] hits1 = hits.getHits();

            for (SearchHit hit : hits1) {
                Map<String, Object> map = hit.getSourceAsMap();
                System.out.println(map.toString());
                String courseId = (String) map.get("id");
                String name = (String) map.get("name");
                String grade = (String) map.get("grade");
                String charge = (String) map.get("charge");
                String pic = (String) map.get("pic");
                String description = (String) map.get("description");
                String teachplan = (String) map.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                resMap.put(courseId, coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resMap;
    }

    private SearchRequest getSearchRequest(String index, String type) {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        return request;
    }

//     ============================================================================

    /**
     * 根据教学计划id查询该教学计划的媒资信息
     *
     * @param arr teachplanId ,教学计划Id 数组
     * @return
     */
    public QueryResponseResult findTeachPlanMediaPub(String[] arr) {
        if(arr== null || arr.length==0){
            RuntimeExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        SearchRequest request = getSearchRequest(meidaIndex, mediaType);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termsQuery("teachplan_id", arr));
        String[] field = getSearchSourceField(mediaSourceField);
        builder.fetchSource(field, new String[]{});
        request.source(builder);
        SearchResponse response = null;
        try {
            response = client.search(request);
        } catch (IOException e) {
            e.printStackTrace();
            RuntimeExceptionCast.cast(MediaCode.SEARCH_MEDIA_ERROR);
        }
        QueryResult result = dealWithMediaHits(response);
        return new QueryResponseResult(CommonCode.SUCCESS, result);
    }

    private QueryResult dealWithMediaHits(SearchResponse response) {
        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        QueryResult<TeachplanMediaPub> result = new QueryResult<>();
        result.setTotal(hits.getTotalHits());
        ArrayList<TeachplanMediaPub> list = new ArrayList<>();
        for (SearchHit hit : hits1) {
            Map<String, Object> map = hit.getSourceAsMap();
            TeachplanMediaPub pub = new TeachplanMediaPub();
//                "media_id": "d511a626d40073d9fbf39c3cd59a875d",
            String mediaId = (String) map.get("media_id");
            pub.setMediaId(mediaId);
//                 "media_fileoriginalname": "19-媒资管理-上传文件-测试.avi",
            String meidaName = (String) map.get("media_fileoriginalname");
            pub.setMediaFileOriginalName(meidaName);
//                 "teachplan_id": "40288581632b593e01632bd606480004",
            String teachPlanId = (String) map.get("teachplan_id");
            pub.setTeachplanId(teachPlanId);
//                 "media_url": "d/5/11a626d40073d9fbf39c3cd59a875d/hls/d511a626d40073d9fbf39c3cd59a875d.avi",
            String mediaUrl = (String) map.get("media_url");
            pub.setMediaUrl(mediaUrl);
//              "courseid": "297e7c7c62b888f00162b8a7dec20000",
            String courseId = (String) map.get("courseid");
            pub.setCourseId(courseId);
            list.add(pub);
        }
        result.setList(list);
        return result;
    }
}
