package com.ping.student.service.impl;

import com.ping.student.mapper.AttachmentMapper;
import com.ping.student.pojo.Attachment;
import com.ping.student.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentServiceImpl implements AttachmentService{
    @Autowired
    private AttachmentMapper attachmentMapper;
    @Override
    public void addAttachment(Attachment attachment) {
        attachmentMapper.insert(attachment);
    }

    @Override
    public void delAttachment(String id) {
        attachmentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateAttachment(Attachment attachment) {
        attachmentMapper.updateByPrimaryKeySelective(attachment);
    }

    @Override
    public Attachment getAttachment(String stuid) {
        return attachmentMapper.selectByStuId(stuid);
    }

    @Override
    public Attachment getOne(String id) {
        return attachmentMapper.selectByPrimaryKey(id);
    }
}
