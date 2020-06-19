package com.edison.springCloudAlibabaDemo.elasticsearch.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
/**新增索引，可支持 XContentBuilder、Map(未添加)、JsonStr作为mapping*/
public class IndexService {
    private static final Settings DEFAULT_SETTINS=Settings.builder()
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0)
            .build();

    @Resource(name="restHighLevelClient")
    RestHighLevelClient restHighLevelClient;

    public boolean createIndex(String indexName,XContentBuilder xContentBuilder) throws Exception{
        return createIndex(indexName,DEFAULT_SETTINS,xContentBuilder);
    }
    public boolean createIndex(String indexName,Settings settings,XContentBuilder xContentBuilder) throws Exception{
        long start=System.currentTimeMillis();
        // 新建创建索引请求对象，然后设置索引类型（ES 7.0 将不存在索引类型）和 mapping 与 index 配置
        CreateIndexRequest request = new CreateIndexRequest(indexName)
                .settings(settings)
                .mapping( xContentBuilder);
        // RestHighLevelClient 执行创建索引
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 判断是否创建成功
        boolean isCreated = createIndexResponse.isAcknowledged();
        log.info("createIndex()共计耗时{}秒",(System.currentTimeMillis()-start)/1000);
        return isCreated;
    }

    public boolean createIndex(String indexName,String jsonStr) throws Exception{
       return createIndex(indexName,DEFAULT_SETTINS,jsonStr);
    }
    public boolean createIndex(String indexName,Settings settings,String jsonStr) throws Exception{
        long start=System.currentTimeMillis();
        // 新建创建索引请求对象，然后设置索引类型（ES 7.0 将不存在索引类型）和 mapping 与 index 配置
        CreateIndexRequest request = new CreateIndexRequest(indexName)
                .settings(settings)
                .mapping( jsonStr, XContentType.JSON);
        // RestHighLevelClient 执行创建索引
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 判断是否创建成功
        boolean isCreated = createIndexResponse.isAcknowledged();
        log.info("是否创建成功："+isCreated);
        log.info("deleteIndex()共计耗时{}秒",(System.currentTimeMillis()-start)/1000);
        return isCreated;
    }

    public void deleteIndex(String indexName) throws Exception{
        long start=System.currentTimeMillis();
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
        log.info("deleteIndex()共计耗时{}秒",(System.currentTimeMillis()-start)/1000);
    }

    public boolean exists(String indexname) throws Exception{
        long start=System.currentTimeMillis();
        GetIndexRequest request = new GetIndexRequest(indexname);
//        request.indices(indexname);
        boolean exists = restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        log.info("exists()共计耗时{}秒",(System.currentTimeMillis()-start)/1000);
        return exists;
    }
}
