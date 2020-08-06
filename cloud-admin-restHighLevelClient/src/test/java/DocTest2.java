import com.edison.springCloudAlibabaDemo.elasticsearch.RestClientApp;
import com.edison.springCloudAlibabaDemo.elasticsearch.entity.Student202009;
import com.edison.springCloudAlibabaDemo.elasticsearch.mapper.Student202009Mapper;
import com.edison.springCloudAlibabaDemo.elasticsearch.service.DocService;
import com.edison.springCloudAlibabaDemo.elasticsearch.utils.NameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import utils.RandomPersonInfoUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(classes = RestClientApp.class)
@RunWith(SpringRunner.class)
@Slf4j
/**批量插入和批量删除和根据查询结果批量删除*/
public class DocTest2 {
    @Autowired
    DocService docService;
    @Autowired
    Student202009Mapper student202009Mapper;
    /**
     *  如果不是springboot自动配置的话，需要进行如下加载，才能获取 sqlSessionFactory，可参考多数据源配置
     *
     * // 实例SessionFactory
     *             sqlSessionFactoryBean = new SqlSessionFactoryBean();
     *             // 配置数据源
     *             sqlSessionFactoryBean.setDataSource(dataSource);
     *
     *             // 加载MyBatis配置文件
     *             PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
     *             // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**\/*.xml)
            *sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(mapperLocations));
     *             // 配置mybatis的config文件(我目前用不上)
             *             // sqlSessionFactoryBean.setConfigLocation("mybatis-config.xml");
     *             sqlSessionFactoryBean.getObject();
            */
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    private static Random RANDOM=new Random(System.currentTimeMillis());
    private static final int TOTAL=1000;//要插入的总的文档数量
    private static final int BATCH_SIZE=3000;//每个批次数量
    private static final String INDEX_NAME = "logs-000001";


    @Test
    /**批量插入es和数据库，根据_id列表批量删除，根据查询条件批量删除测试*/
    public void docTest_addbatch() {
        RandomPersonInfoUtil randomPersonInfoUtil=new RandomPersonInfoUtil();
        AtomicLong atomicInteger=new AtomicLong(100001L);
        //往es的student_202009和mysql同时写入数据
        String docId=null;
        List<Student202009> list=new ArrayList<>(1500);
        int batch=1;

//        SqlSession sqlsession=sqlSessionFactory.openSession(ExecutorType.BATCH,false);
//        Student202009Mapper student202009Mapper1=sqlsession.getMapper(Student202009Mapper.class);

        for(int i=0;i<TOTAL;i++) {
            // 创建学生信息
            Student202009 stu=new Student202009();
            int age=getRandomAge();
            stu.setAge(age);
            stu.setBirthdate((2020-age)+"-"+getRandomMonthDay());
            stu.setCreatetime(new Date());
            stu.setHeight((float)(1.40+RANDOM.nextInt(60)*0.01));
            stu.setHomeaddr(RandomPersonInfoUtil.getRandomLocation());
            stu.setSex(getRandomSex());
            NameUtils surnameUtils = new NameUtils();
            stu.setName(surnameUtils.getSurname()+surnameUtils.getName(stu.getSex()));
            stu.setPhone(RandomPersonInfoUtil.getRandomTelephoneNumber());
            stu.setRemark("");
            stu.setSid("202009"+String.format("%014d",atomicInteger.getAndIncrement())); //学号
//            log.info(stu.toString());

            list.add(stu);


            if(list.size()==BATCH_SIZE||i==TOTAL-1){
                try {
                    long start=System.currentTimeMillis();
                    //5-15M大小即可 所以1500这个数据还可以往大了调整
                    docService.addDocumentsBatch(INDEX_NAME, list);
                    long end=System.currentTimeMillis();
                    log.info("插入es第{}个批次1500条，耗时{}秒",batch,(end-start)/1000);
                    start=System.currentTimeMillis();
                    //mysql默认接受sql的大小是1048576(1M) 所以1500条是没问题的
                    //insert into xxx values (...)(...)，这是标准的mysql导入导出写法，这是最快的。
                    // 数据量较大的情况下，ExecutorType.BATCH 比上种用法慢上100倍不止。
//                    student202009Mapper.insertBatch(list); //foreach方式竟然比BATCH还要快得多  只需3秒。。
//                    sqlsession.commit();
                    end=System.currentTimeMillis();
                    log.info("插入数据库第{}个批次{}条，耗时{}秒",batch,BATCH_SIZE,(end-start)/1000);
                    batch++;
                } catch (Exception e) {
                    e.printStackTrace();
                }


                list.clear();
            }
        }


    }

    /**根据id列表删除*/
    @Test
    public void docTest_delbatchByIds() throws Exception{
        String indexName = "student_202009";
        List<String> ids=new ArrayList<>(8);
        ids.add("-4-Fk3MBwzZ693qXgf8j");
        ids.add("_I_sk3MBwzZ693qXmv8d");
        docService.delDocumentsBatchByIds(indexName,ids);
    }

    /**根据查询条件删除*/
    @Test
    public void docTest_delbatchByQuery() throws Exception{
        String indexName = "student_202009";
        List<String> ids=new ArrayList<>(8);
        docService.delDocumentsBatchByQuery(indexName, QueryBuilders.matchAllQuery());
    }


    public int getRandomAge(){
        int[] ages={15,16,17,18,19,20,21};
        return ages[RANDOM.nextInt(ages.length)];
    }

    public String getRandomMonthDay(){
        int[] months={1,2,3,4,5,6,7,8,9,10,11,12};
        int[] days={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        int month=months[RANDOM.nextInt(months.length)];
        int day=days[RANDOM.nextInt(days.length)];
        if(month==2){
            day=day%29;
        }else if(month ==4 || month==6 || month==9 ||month==11){
            day=day%31;
        }
        if(day==0){
            day++;
        }
        return String.format("%02d-%02d",month,day);
    }

    public String getRandomSex(){
        String[] sexes={"男","女"};
        return sexes[RANDOM.nextInt(2)];
    }


    public String getRandomNumbers(int length){
        int[] nums={1,2,3,4,5,6,7,8,9,0};
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<length;i++){
            sb.append(nums[RANDOM.nextInt(nums.length)]);
        }
        return sb.toString();
    }
}
