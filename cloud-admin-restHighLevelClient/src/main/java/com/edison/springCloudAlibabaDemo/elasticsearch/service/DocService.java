package com.edison.springCloudAlibabaDemo.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**文档的增删改查*/
@Slf4j
@Service
public class DocService {
    private static final String DEFAULT_DOCTYPE="_doc";

    @Resource(name="restHighLevelClient")
    RestHighLevelClient restHighLevelClient;


    /**
     * 增加文档信息
     */
    public  String addDocument(Object object,String indexName,String docId) {
        try {
            // 创建索引请求对象
            IndexRequest indexRequest;
            if(StringUtils.isEmpty(docId)){
                indexRequest = new IndexRequest(indexName,DEFAULT_DOCTYPE); //此时id自动生成=UUIDs.base64UUID()
            }else {
                indexRequest= new IndexRequest(indexName,DEFAULT_DOCTYPE,docId); //这个指定插入的doc的id
            }
            // 将对象转换为 byte 数组
            byte[] json = JSON.toJSONBytes(object);
            // 设置文档内容
            indexRequest.source(json, XContentType.JSON);
            // 执行增加文档
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            int status=response.status().getStatus();
            if(status== RestStatus.OK.getStatus()|| status==RestStatus.ACCEPTED.getStatus()||status==RestStatus.CREATED.getStatus()){
                log.info("创建成功{}",status);//成功返回的是201
                return response.getId();
            }else{
                log.info("创建失败{}",status);
                return null;
            }
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    /**
     * 获取文档信息
     */
    public <T> T getDocument(String indexName,String docId,Class<T> clazz) {
        try {
            // 获取请求对象
            GetRequest getRequest = new GetRequest(indexName, DEFAULT_DOCTYPE,docId);
            // 获取文档信息
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            // 将 JSON 转换成对象
            if (getResponse.isExists()) {
                T object = JSON.parseObject(getResponse.getSourceAsBytes(), clazz);
                log.info("员工信息：{}", object);
                return object;
            }
        } catch (IOException e) {
            log.error("", e);
            return null;
        }
        return null;
    }

    /**
     * 更新文档信息
     */
    public void updateDocument(Object object,String indeName,String docId) {
        try {
            // 创建索引请求对象
            UpdateRequest updateRequest = new UpdateRequest(indeName, DEFAULT_DOCTYPE, docId);

            // 将对象转换为 byte 数组
            byte[] json = JSON.toJSONBytes(object);
            // 设置更新文档内容
            updateRequest.doc(json, XContentType.JSON);
            // 执行更新文档
            UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("创建状态：{}", response.status());
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文档信息
     */
    public void deleteDocument(String indexName,String type,String docId) {
        try {
            // 创建删除请求对象
            DeleteRequest deleteRequest = new DeleteRequest(indexName, DEFAULT_DOCTYPE, docId);
            // 执行删除文档
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除状态：{}", response.status());
        } catch (IOException e) {
            log.error("", e);
        }
    }


}
