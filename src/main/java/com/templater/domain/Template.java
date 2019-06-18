package com.templater.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
public class Template {
    private Long id;
    private String name;
    private LocalDate dateOfCreation;
    private Long numberOfParts;
    private Boolean privateTemplate;
    private User user;
    private Set<Paragraph> paragraphs;
    private Set<DocTable> docTables;
    private Set<Picture> pictures;
    private Set<Document> documents;
    private Set<Placeholder> placeholders;
    private Set<PartGroup> partGroups;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<Placeholder> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Set<Placeholder> placeholders) {
        this.placeholders = placeholders;
    }

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "template")//todo переделать CascadeType во всех сущностях
    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<PartGroup> getPartGroups() {
        return partGroups;
    }

    public void setPartGroups(Set<PartGroup> partGroups) {
        this.partGroups = partGroups;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Set<Paragraph> paragraphs) {

            this.paragraphs=paragraphs;

    }
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<DocTable> getDocTables() {
        return docTables;
    }

    public void setDocTables(Set<DocTable> docTables) {

            this.docTables=docTables;

    }
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {

            this.pictures=pictures;

    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(updatable=false)
    @CreationTimestamp
    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Long getNumberOfParts() {
        return numberOfParts;
    }

    public void setNumberOfParts(Long numberOfParts) {
        this.numberOfParts = numberOfParts;
    }

    public Boolean getPrivateTemplate() {
        return privateTemplate;
    }

    public void setPrivateTemplate(Boolean privateTemplate) {
        this.privateTemplate = privateTemplate;
    }

    public Template(){}

    public Template(Template template){
        this.setPrivateTemplate(template.getPrivateTemplate());
        this.setName(template.getName());
        this.setNumberOfParts(template.getNumberOfParts());
        this.placeholders = new HashSet<>();
        this.pictures = new HashSet<>();
        this.paragraphs = new HashSet<>();
        this.docTables = new HashSet<>();
        for (Paragraph p:template.getParagraphs()) {
            addParagraph(new Paragraph(p));
        }
        for (Picture p:template.getPictures()) {
            addPicture(new Picture(p));
        }
        for (DocTable p:template.getDocTables()) {
            addTable(new DocTable(p));
        }
        for (Placeholder p:template.getPlaceholders()) {
            addPlaceholder(new Placeholder(p));
        }
    }

    public void addParagraph(Paragraph paragraph){
        this.paragraphs.add(paragraph);
        paragraph.setTemplate(this);
    }

    public void addTable(DocTable table){
        this.docTables.add(table);
        table.setTemplate(this);
    }

    public void addPicture(Picture picture){
        this.pictures.add(picture);
        picture.setTemplate(this);
    }

    public void addPlaceholder(Placeholder placeholder){
        this.placeholders.add(placeholder);
        placeholder.setTemplate(this);
    }
}
