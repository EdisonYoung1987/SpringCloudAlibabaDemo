import com.edison.springCloudAlibabaDemo.elasticsearch.RestClientApp;
import com.edison.springCloudAlibabaDemo.elasticsearch.entity.UserInfo;
import com.edison.springCloudAlibabaDemo.elasticsearch.service.DocService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest(classes = RestClientApp.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DocTest {
    @Autowired
    DocService docService;

    @Test
    public void docTest() {
        String indexName = "mydlq-user";
        String docId=null;
        for(int i=0;i<1000;i++) {
            // 创建员工信息
            UserInfo userInfo = new UserInfo();
            userInfo.setName("张三");
            int age=i%100+1;
            userInfo.setAge(age);
            userInfo.setSalary(100.00f+i);
            userInfo.setAddress("北京市");
            userInfo.setRemark("来自北京市的张先生"+i);
            userInfo.setCreateTime(new Date());
            userInfo.setBirthDate(LocalDate.now().minusYears(age).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            docId=docService.addDocument(userInfo,indexName);

            if(docId==null){
                log.error("创建对象失败");
                return;
            }else{
                log.info("docId={}",docId);
            }
        }


        if(docId==null){
            log.error("创建对象失败");
            return;
        }else{
            log.info("docId={}",docId);
        }

        UserInfo userInfo=docService.getDocument(indexName,docId,UserInfo.class);
        log.info(userInfo.toString());

        // 设置员工更新信息
        userInfo = new UserInfo();
        userInfo.setSalary(200.00f);
        userInfo.setAddress("北京市海淀区");
        docService.updateDocument(userInfo,indexName,docId);

        log.info("经过更新后...");
        userInfo=docService.getDocument(indexName,docId,UserInfo.class);
        log.info(userInfo.toString());
    }
}
