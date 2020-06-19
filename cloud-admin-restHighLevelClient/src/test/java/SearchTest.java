import com.edison.springCloudAlibabaDemo.elasticsearch.RestClientApp;
import com.edison.springCloudAlibabaDemo.elasticsearch.entity.UserInfo;
import com.edison.springCloudAlibabaDemo.elasticsearch.service.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = RestClientApp.class)
@RunWith(SpringRunner.class)
@Slf4j
public class SearchTest {
    @Autowired
    SearchTermService searchTermService;
    @Autowired
    SearchMatchService searchMatchService;
    @Autowired
    SearchRangeService searchRangeService;
    @Autowired
    SearchBoolService searchBoolService;
    @Autowired
    SearchAggrMetricService searchAggrMetricService;

    @Test
    public void testTermQuery(){
        //目前库里面只有两个[北京市海淀区]和一个[北京市]
        List<UserInfo> userInfos=searchTermService.termsQuery("mydlq-user","address.keyword", Collections.singletonList("北京"), UserInfo.class);
        for(UserInfo userInfo:userInfos){//精确查找不分词，且索引也未分词的情况下，北京是查不到数据的
            log.info(userInfo.toString());
        }
        log.info("------------------------------------------");
        userInfos=searchTermService.termsQuery("mydlq-user","address.keyword", Collections.singletonList("北京市海淀区"), UserInfo.class);
        for(UserInfo userInfo:userInfos){
            log.info(userInfo.toString());
        }

        log.info("------------------------------------------");
        List<String> conditions=new ArrayList<>(4);
        conditions.add("北京市海淀区");
        conditions.add("北京市");
        userInfos=searchTermService.termsQuery("mydlq-user","address.keyword", conditions, UserInfo.class);
        for(UserInfo userInfo:userInfos){
            log.info(userInfo.toString());
        }
    }

    @Test
    public void testMatchQuery(){

        List<UserInfo> list=searchMatchService.matchAllQuery("mydlq-user",0,10,new HashMap<String, SortOrder>(){
            {put("salary",SortOrder.ASC);
            }},UserInfo.class);
        for(UserInfo userInfo:list){
            log.info(userInfo.toString());
        }
        log.info("-----------------------------");
        list=searchMatchService.matchQuery("mydlq-user","address","北京",0,10,new HashMap<String, SortOrder>(){
            {put("salary",SortOrder.ASC);
            }},UserInfo.class);
        for(UserInfo userInfo:list){
            log.info(userInfo.toString());
        }
        log.info("-----------------------------");
        searchMatchService.matchPhraseQuery();//这两个还灭改写
        log.info("-----------------------------");
        searchMatchService.matchMultiQuery("","");
    }

    @Test//范围查找
    public void testRangeQuery(){
        searchRangeService.rangeQuery();
        log.info("------------------------------");
        searchRangeService.dateRangeQuery();
    }

    @Test//多条件联合查询
    public void testBoolQuery(){
        searchBoolService.boolQuery();
    }

    @Test//聚合查询
    public void testAggsQuery(){
        searchAggrMetricService.aggregationStats();
        log.info("--------------------------------");
        searchAggrMetricService.aggregationPercentiles();
    }
}
