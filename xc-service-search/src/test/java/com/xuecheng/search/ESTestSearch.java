package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @program: XcEduCode
 * @description: 测试elastic search DSL(domain special language)查询常用方法
 * @author: WhyWhatHow
 * @create: 2020-04-04 17:18
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ESTestSearch {

    // 测试添加索引库
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;
    private String doc;
    private String xcCourse;

    /**
     * 利用dsl方法查询,post提交
     */
    @Test
    public void searchAll() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);

    }


    @Test
    public void searchAllByPage() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        searchSourceBuilder.from(1);
        searchSourceBuilder.size(2);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);

    }

    /**
     * term 不分词处理,直接搜索
     *
     * @throws IOException
     */
    @Test
    public void searchAllByTermQuery() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "spring"));
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    /**
     * 单字段匹配
     * metchQuery : 先分词,然后搜索
     * minimumShouldMatch("80%") : gt 分词数* 80% 的数据显示
     *
     * @throws IOException
     */
    @Test
    public void searchAllByMetchQuery() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发框架").operator(Operator.OR).minimumShouldMatch("80%"));
//        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    @Test
    public void searchAllByMultiQuery() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery("spring 框架", "name", "description").minimumShouldMatch("50%").field("name", 100); // 设置多子弹匹配
        searchSourceBuilder.query(builder);
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    /**
     * bool 查询
     * must：表示必须，多个查询条件必须都满足。（通常使用must）
     * should：表示或者，多个查询条件只要有一个满足即可。
     * must_not：表示非。
     *
     * @throws IOException
     */
    @Test
    public void searchAllByBoolQuery() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery("spring 框架", "name", "description").minimumShouldMatch("50%").field("name", 100); // 设置多子弹匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(builder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    /**
     * filter : term , range
     * boolQueryBuilder.filter(termQueryBuilder);
     * boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(60).lt(100));
     *
     * @throws IOException
     */
    @Test
    public void searchAllByFilter() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery("spring 框架", "name", "description").minimumShouldMatch("50%").field("name", 100); // 设置多子弹匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(builder);
        boolQueryBuilder.filter(termQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(60).lt(100));
        searchSourceBuilder.query(boolQueryBuilder);
        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    /**
     * 测试排序, studymodel  desc , price asc
     * searchSourceBuilder.sort("studymodel", SortOrder.DESC);
     * searchSourceBuilder.sort("price", SortOrder.ASC);
     *
     * @throws IOException
     */
    @Test
    public void searchAllBySort() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(0).lt(100));
        searchSourceBuilder.query(boolQueryBuilder);
        // 4 设置排序

        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHits(response);
    }

    /**
     * 高亮显示结果
     *
     * @throws IOException
     */
    @Test
    public void searchAllByHighLight() throws IOException {
        // 1 初始化搜索请求对象
        SearchRequest request = initSearchRequest("xc_course", "doc");
        // 2 初始化搜索原构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 3 设置搜索条件
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery("开发框架", "name", "description").minimumShouldMatch("50%").field("name", 100); // 设置多子弹匹配
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(builder);
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(60).lt(100));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("price", SortOrder.ASC);

        // 4 设置搜索结果
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});   // 5 获取返回结果
        // 5 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request);
        // 6 返回hits,并遍历
        soutHitsAndHighLight(response);
    }

    private void soutHitsAndHighLight(SearchResponse response) {
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            System.out.println(highlightFields);
            System.out.println(documentFields.getId());
            Map<String, Object> map = documentFields.getSourceAsMap();
            System.out.println(map);
        }
    }


    private SearchRequest initSearchRequest(String index, String type) {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        return request;
    }

    /**
     * 遍历hits
     *
     * @param response
     */
    private void soutHits(SearchResponse response) {
        SearchHits hits = response.getHits();
//        System.out.println(hits.totalHits);
        long totalHits = hits.getTotalHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            System.out.println(documentFields.getId());
            Map<String, Object> map = documentFields.getSourceAsMap();
            System.out.println(map);
        }
    }
}
