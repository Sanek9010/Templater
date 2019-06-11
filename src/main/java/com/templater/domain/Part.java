package com.templater.domain;

public interface Part {
    Template getTemplate();
    String getContentXml();
    Long getNumberInTemplate();
    Long getId();
    void setContentXml(String contentXml);
    String getType();
    String getPicture();
}
