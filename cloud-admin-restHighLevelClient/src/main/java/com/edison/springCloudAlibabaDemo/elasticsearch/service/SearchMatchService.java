package com.edison.springCloudAlibabaDemo.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.edison.springCloudAlibabaDemo.elasticsearch.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**模糊查找：<p/>
 * match_all:查询所有，可以增加条数控制以及排序
 * match-模糊查询：先对输入进行分词，对分词后的结果进行查询，文档只要包含match查询条件的一部分就会被返回。<p/>
 * "match_phrase"-先对输入进行分词，分词后所有term都出现在待查询文档之中，且顺序和输入一致
 * multi_match:内容多字段查询*/
@Slf4j
@Service
public class SearchMatchService {
    @Resource(name="restHighLevelClient")
    RestHighLevelClient restHighLevelClient;

    /**
     * 匹配查询符合条件的所有数据，并设置分页
     */
    public <T>  List<T> matchAllQuery(String indexName, int start, int offset, Map<String,SortOrder> sortMap,Class<T> clazz) {
        List<T> list=new ArrayList<>();

        try {
            // 构建查询条件
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            // 创建查询源构造器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchAllQueryBuilder);

            // 设置分页
            searchSourceBuilder.from(start);
            searchSourceBuilder.size(offset);

            // 设置排序
//            searchSourceBuilder.sort("salary", SortOrder.ASC);
            Set<String> keys= sortMap.keySet();
            for(String key:keys){
                searchSourceBuilder.sort(key,sortMap.get(key));
            }

            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);

            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().totalHits > 0) {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    // 将 JSON 转换成对象
                    T obj = JSON.parseObject(hit.getSourceAsString(), clazz);
                    list.add(obj);
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }finally {
            return list;
        }
    }

    /**
     * 匹配查询数据
     */
    public <T> List<T> matchQuery(String indexName,String field, Object condition,int start, int offset, Map<String,SortOrder> sortMap,Class<T> clazz) {
        List<T> list=new ArrayList<>();

        try {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(QueryBuilders.matchQuery("address", "*通州区"));
            searchSourceBuilder.query(QueryBuilders.matchQuery(field,condition));

            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);

            // 设置分页
            searchSourceBuilder.from(start);
            searchSourceBuilder.size(offset);

            // 设置排序
            Set<String> keys= sortMap.keySet();
            for(String key:keys){
                searchSourceBuilder.sort(key,sortMap.get(key));
            }

            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().totalHits > 0) {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    // 将 JSON 转换成对象
                    T object = JSON.parseObject(hit.getSourceAsString(), clazz);
                    list.add(object);
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }finally {
            return list;
        }
    }

    /**
     * 词语匹配查询
     */
    public void matchPhraseQuery() {
        try {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("address", "北京市海淀区"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().totalHits > 0) {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    // 将 JSON 转换成对象
                    UserInfo userInfo = JSON.parseObject(hit.getSourceAsString(), UserInfo.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 内容在多字段中进行查询
     */
    public void matchMultiQuery(String indexName,String... fieldNames) {
        try {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery("北京市", "address", "remark"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().totalHits > 0) {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    // 将 JSON 转换成对象
                    UserInfo userInfo = JSON.parseObject(hit.getSourceAsString(), UserInfo.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }

    }
}
