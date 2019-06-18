package com.templater.domain;

import javax.persistence.*;

@Entity
public class DocTable implements Part {

    private Long id;
    private Long numberInTemplate;
    private String contentXml;
    private Template template;
    private TableStyle tableStyle;
    private PartGroup partGroup;

    @ManyToOne
    public PartGroup getPartGroup() {
        return partGroup;
    }

    public void setPartGroup(PartGroup partGroup) {
        this.partGroup = partGroup;
    }

    @ManyToOne
    public TableStyle getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(TableStyle tableStyle) {
        this.tableStyle = tableStyle;
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
        return "Table";
    }

    @Transient
    @Override
    public String getPicture() {
        return "";
    }

    public DocTable(){}

    public DocTable(DocTable table){
        this.numberInTemplate = table.getNumberInTemplate();
        this.contentXml = table.getContentXml();
        this.tableStyle = table.getTableStyle();
    }
}
