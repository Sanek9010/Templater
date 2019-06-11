package com.templater.service;

public class RequestContent{
    private String editorType;
    private String content;
    private String styleId;
    private Long numberOfPart;
    private byte[] picture;

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(String editorType) {
        this.editorType = editorType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public Long getNumberOfPart() {
        return numberOfPart;
    }

    public void setNumberOfPart(Long numberOfPart) {
        this.numberOfPart = numberOfPart;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
