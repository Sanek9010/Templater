package com.templater.domain;

import javax.persistence.*;

@Entity
public class Paragraph implements Part {
    private Long id;
    private Long numberInTemplate;
    private String contentXml;
    private Template template;
    private ParagraphStyle paragraphStyle;

    @ManyToOne
    public ParagraphStyle getParagraphStyle() {
        return paragraphStyle;
    }

    public void setParagraphStyle(ParagraphStyle paragraphStyle) {
        this.paragraphStyle = paragraphStyle;
    }

    @ManyToOne
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @Column(columnDefinition = "TEXT")
    public String getContentXml() {
        return contentXml;
    }

    public void setContentXml(String contentXml) {
        this.contentXml = contentXml;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumberInTemplate() {
        return numberInTemplate;
    }

    public void setNumberInTemplate(Long numberInTemplate) {
        this.numberInTemplate = numberInTemplate;
    }

    @Transient
    @Override
    public String getType() {
        return "Paragraph";
    }

    @Transient
    @Override
    public String getPicture() {
        return "";
    }
}
