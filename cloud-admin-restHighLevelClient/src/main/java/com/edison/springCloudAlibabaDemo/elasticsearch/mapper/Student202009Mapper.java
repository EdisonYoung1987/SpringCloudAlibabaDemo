package com.edison.springCloudAlibabaDemo.elasticsearch.mapper;

import com.edison.springCloudAlibabaDemo.elasticsearch.entity.Student202009;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface Student202009Mapper {
    int deleteByPrimaryKey(String sid);

    int insert(Student202009 record);

    int insertSelective(Student202009 record);

    Student202009 selectByPrimaryKey(String sid);

    int updateByPrimaryKeySelective(Student202009 record);

    int updateByPrimaryKey(Student202009 record);
    int insertBatch(List<Student202009> list);
}