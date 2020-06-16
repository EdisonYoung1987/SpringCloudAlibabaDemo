package com.edison.springCloudAlibabaDemo.elasticsearch.index;

import com.edison.springCloudAlibabaDemo.elasticsearch.RestClientApp;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = RestClientApp.class)
@RunWith(SpringRunner.class)
@Slf4j
public class IndexTest {
    /**PUT /mydlq-user
     {
     "mappings": {
        "doc": {
            "dynamic": true,
            "properties": {
                "name": {
                    "type": "text",
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        }
                    }
                },*/
    @Resource(name="restHighLevelClient")
    RestHighLevelClient restHighLevelClient;
    @Test
    public void creatIndex() throws Exception{
        log.info("开始");
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject()
                    .field("dynamic", true)
                    .startObject("properties")
                        .startObject("name")
                            .field("type","text")
                            .startObject("fields")
                                .startObject("keyword")
                                    .field("type","keyword")
                                .endObject()
                            .endObject()
                        .endObject()
                        .startObject("address")
                            .field("type","text")
                            .startObject("fields")
                                .startObject("keyword")
                                    .field("type","keyword")
                                .endObject()
                            .endObject()
                        .endObject()
                        .startObject("remark")
                            .field("type","text")
                            .startObject("fields")
                                .startObject("keyword")
                                    .field("type","keyword")
                                 .endObject()
                            .endObject()
                        .endObject()
                        .startObject("age")
                            .field("type","integer")
                        .endObject()
                        .startObject("salary")
                            .field("type","float")
                        .endObject()
                        .startObject("birthDate")
                            .field("type","date")
                            .field("format", "yyyy-MM-dd")
                        .endObject()
                        .startObject("createTime")
                            .field("type","date")
                        .endObject()
                    .endObject()
                .endObject();
        // 创建索引配置信息，配置
        Settings settings = Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .build();
        // 新建创建索引请求对象，然后设置索引类型（ES 7.0 将不存在索引类型）和 mapping 与 index 配置
        CreateIndexRequest request = new CreateIndexRequest("mydlq-user").settings(settings);
//            request.mapping( "doc",builder);//"doc"是文档类型，高版本已经弃用了
        request.mapping( xContentBuilder);

        //还可以mapping其他类型
//        request.mapping(Map<String,Object> map);
//        request.mapping(String jsonString, XContentType.JSON);

        // RestHighLevelClient 执行创建索引
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 判断是否创建成功
        boolean isCreated = createIndexResponse.isAcknowledged();
        log.info("是否创建成功："+isCreated);

    }
}
