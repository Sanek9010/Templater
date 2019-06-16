package com.templater.domain;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Base64;

@Entity
public class Placeholder {
    private Long id;
    private String type;//todo сделать enum
    private String name;
    private String contentXml;
    private Template template;
    private Document document;
    private Boolean filled;
    private MultipartFile pictureFile;
    private byte[] pictureBytes;

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "TEXT")
    public String getContentXml() {
        return contentXml;
    }

    public void setContentXml(String contentXml) {
        this.contentXml = contentXml;
    }

    @ManyToOne
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @ManyToOne
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }


    public Boolean getFilled() {
        return filled;
    }

    public void setFilled(Boolean filled) {
        this.filled = filled;
    }

    @Transient
    public MultipartFile getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(MultipartFile pictureFile) {
        this.pictureFile = pictureFile;
    }

    @Lob
    public byte[] getPictureBytes() {
        return pictureBytes;
    }

    public void setPictureBytes(byte[] pictureBytes) {
        this.pictureBytes = pictureBytes;
    }

    public Placeholder(){}

    public Placeholder(Placeholder placeholder){
        this.contentXml = placeholder.getContentXml();
        this.filled = placeholder.getFilled();
        this.name = placeholder.getName();
        this.pictureBytes = placeholder.getPictureBytes();
        this.pictureFile = placeholder.getPictureFile();
        this.type = placeholder.getType();
    }
}
