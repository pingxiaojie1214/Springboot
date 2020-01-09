package com.ping.student.service;

import com.ping.student.pojo.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StudentService {
    void addStu(Student student);

    Student getOne(String id);

    void updateStu(Student student);

    int getAllCount(Map<String,Object> params);

    List<Student> getAll(Map<String,Object> params);

    List<Student> getAllStu(Map<String,Object> params);

    void delStu(String id);
}
