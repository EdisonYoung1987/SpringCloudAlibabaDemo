import com.edison.springCloudAlibabaDemo.elasticsearch.RestClientApp;
import com.edison.springCloudAlibabaDemo.elasticsearch.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = RestClientApp.class)
@RunWith(SpringRunner.class)
@Slf4j
public class IndexTest {
    @Autowired
    IndexService indexService;

    /**PUT /mydlq-user
     {
        "mappings": {
        "_doc": { //这个就是索引类型，默认_doc
            "dynamic": true,
            "properties": {
                "name": {
                    "type": "text",
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        }
                    }
                }
            }
        }
     },*/
    @Test
    public void indexTest() {
        try {
            String indexName = "mydlq-user2";
            boolean exists = indexService.exists(indexName);
            if (exists) {
                log.info("该索引{}已存在,进行删除操作", indexName);
                indexService.deleteIndex(indexName);
            }
            log.info("开始重新创建索引{}", indexName);
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.startObject()
                    .field("dynamic", true)
                    .startObject("properties")
                    .startObject("name")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    .startObject("address")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    .startObject("remark")
                    .field("type", "text")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    .startObject("age")
                    .field("type", "integer")
                    .endObject()
                    .startObject("salary")
                    .field("type", "float")
                    .endObject()
                    .startObject("birthDate")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()
                    .startObject("createTime")
                    .field("type", "date")
                    .endObject()
                    .endObject()
                    .endObject();
            // 创建索引配置信息，配置
            Settings settings = Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
                    .build();

            //创建索引
            boolean isCreated = indexService.createIndex(indexName, settings, xContentBuilder);

            log.info("是否创建成功：" + isCreated);
        }catch (Exception e){
            e.printStackTrace();
            log.info("发生异常");
        }
    }
}
