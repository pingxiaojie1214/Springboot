package com.ping.student.mapper;

import com.ping.student.pojo.Student;

import java.util.List;
import java.util.Map;

public interface StudentMapper {
    int deleteByPrimaryKey(String id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    int getAllCount(Map<String,Object> params);

    List<Student> getAll(Map<String,Object> params);

    List<Student> getAllStu(Map<String,Object> params);
}