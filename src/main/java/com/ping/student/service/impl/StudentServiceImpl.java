package com.ping.student.service.impl;

import com.ping.student.mapper.StudentMapper;
import com.ping.student.pojo.Student;
import com.ping.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public void addStu(Student student) {
        student.setId(UUID.randomUUID().toString());
        studentMapper.insert(student);
    }

    @Override
    public Student getOne(String id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateStu(Student student) {
        studentMapper.updateByPrimaryKeySelective(student);
    }

    @Override
    public int getAllCount(Map<String, Object> params) {
        return studentMapper.getAllCount(params);
    }

    @Override
    public List<Student> getAll(Map<String, Object> params) {
        return studentMapper.getAll(params);
    }

    @Override
    public List<Student> getAllStu(Map<String, Object> params) {
        return studentMapper.getAllStu(params);
    }


    @Override
    public void delStu(String id) {
        studentMapper.deleteByPrimaryKey(id);
    }
}
