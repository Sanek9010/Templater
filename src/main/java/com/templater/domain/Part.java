package com.templater.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

public interface Part {
    Template getTemplate();
    String getContentXml();
    Long getNumberInTemplate();
    Long getId();
    void setContentXml(String contentXml);
    String getType();
    String getPicture();
    PartGroup getPartGroup();
}
