package com.templater.domain;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
public class Paragraph implements Part {
    private Long id;
    private Long numberInTemplate;
    private String contentXml;
    private ParagraphStyle paragraphStyle;
    private TableStyle tableStyle;
    private PartGroup partGroup;

    public Paragraph(){}

    public Paragraph(Paragraph paragraph){
        this.contentXml = paragraph.getContentXml();
        this.numberInTemplate = paragraph.getNumberInTemplate();
        this.paragraphStyle = paragraph.getParagraphStyle();
        this.tableStyle = paragraph.getTableStyle();
    }

    @ManyToOne
    public PartGroup getPartGroup() {
        return partGroup;
    }

    public void setPartGroup(PartGroup partGroup) {
        this.partGroup = partGroup;
    }

    @ManyToOne
    public ParagraphStyle getParagraphStyle() {
        return paragraphStyle;
    }

    public void setParagraphStyle(ParagraphStyle paragraphStyle) {
        this.paragraphStyle = paragraphStyle;
    }

    @ManyToOne
    public TableStyle getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(TableStyle tableStyle) {
        this.tableStyle = tableStyle;
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
