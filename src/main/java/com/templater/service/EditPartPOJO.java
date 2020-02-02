package com.templater.service;

public class EditPartPOJO {

    private Integer partId;
    private String content;
    private String styleId;
    private String tableStyleId;

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
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

    public String getTableStyleId() {
        return tableStyleId;
    }

    public void setTableStyleId(String tableStyleId) {
        this.tableStyleId = tableStyleId;
    }
}
