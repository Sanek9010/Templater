package com.templater.domain;

import com.templater.service.CustomMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.persistence.*;

@Entity
public class Picture implements Part {
    private String contentXml;
    private Long id;
    private Long numberInTemplate;
    private Template template;
    @Column(length=100000)
    private byte[] pictureBytes;
    private PartGroup partGroup;

    public Picture(){}

    public Picture(Picture picture){
        this.contentXml = picture.getContentXml();
        this.numberInTemplate = picture.getNumberInTemplate();
        this.pictureBytes = picture.getPictureBytes();
    }

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
    public byte[] getPictureBytes() {
        return pictureBytes;
    }

    public void setPictureBytes(byte[] pictureFile) {
        this.pictureBytes = pictureFile;
    }

    @Transient
    @Override
    public String getType() {
        return "Picture";
    }

    @Transient
    @Override
    public String getPicture() {
        byte[] encoded = Base64.getEncoder().encode(getPictureBytes());
        return new String(encoded);
    }



}
