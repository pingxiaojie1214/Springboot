package com.ping.student.service;

import com.ping.student.pojo.Attachment;

public interface AttachmentService {
    void addAttachment(Attachment attachment);

    void delAttachment(String id);

    void updateAttachment(Attachment attachment);

    Attachment getAttachment(String stuid);

    Attachment getOne(String id);

}
