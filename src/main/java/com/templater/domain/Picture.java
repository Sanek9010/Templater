package com.templater.domain;

import java.util.Base64;
import javax.persistence.*;

@Entity
public class Picture implements Part {
    private String contentXml;
    private Long id;
    private Long numberInTemplate;
    private Template template;
    @Column(length=100000)
    private byte[] pictureFile;

    private PartGroup partGroup;

    @ManyToOne
    public PartGroup getPartGroup() {
        return partGroup;
    }

    public void setPartGroup(PartGroup partGroup) {
        this.partGroup = partGroup;
    }


    @ManyToOne
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

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

    @Lob
    public byte[] getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(byte[] pictureFile) {
        this.pictureFile = pictureFile;
    }

    @Transient
    @Override
    public String getType() {
        return "Picture";
    }

    @Transient
    @Override
    public String getPicture() {
        byte[] encoded = Base64.getEncoder().encode(getPictureFile());
        return new String(encoded);
    }

    public Picture(){}

    public Picture(Picture picture){
        this.contentXml = picture.getContentXml();
        this.numberInTemplate = picture.getNumberInTemplate();
        this.pictureFile = picture.getPictureFile();
    }
}
