package com.templater.domain;

public interface Part {
    Template getTemplate();
    String getContentXml();
    Long getNumberInTemplate();
    Long getId();
}
