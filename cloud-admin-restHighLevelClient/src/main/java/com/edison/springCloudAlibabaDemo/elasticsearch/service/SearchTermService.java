package com.edison.springCloudAlibabaDemo.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**精确查找*/
@Slf4j
@Service
public class SearchTermService {
    @Resource(name="restHighLevelClient")
    RestHighLevelClient restHighLevelClient;

    /**
     * 精确查询（查询条件不会进行分词，但是查询内容可能会分词，导致查询不到）<p/>
     * 支持多个内容在一个字段中进行查询
     */
    public <T> List<T> termsQuery(String indexName, String keyword, List<String> conditions,Class<T> clazz) {
        List<T> list=new ArrayList<>(16);
        try {
            // 构建查询条件（注意：termsQuery 支持多种格式查询，如 boolean、int、double、string 等，这里使用的是 string 的查询）
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if(conditions.size()==1){
                searchSourceBuilder.query(QueryBuilders.termQuery(keyword,conditions.get(0)));
            }else{
                searchSourceBuilder.query(QueryBuilders.termsQuery(keyword,conditions));
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
                    T object = JSON.parseObject(hit.getSourceAsString(), clazz);
                    list.add(object);
                }
                return list;
            }
        } catch (IOException e) {
            log.error("", e);
            return list;
        }
        return list;
    }

}
