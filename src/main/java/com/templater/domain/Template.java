package com.templater.domain;

import com.templater.repositories.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

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
    private Set<Picture> pictures;
    private Set<Document> documents;
    private Set<Placeholder> placeholders;
    private Set<PartGroup> partGroups;

    public Template(){}

    public Template(Template template){
        this.setPrivateTemplate(template.getPrivateTemplate());
        this.setName(template.getName());
        this.setNumberOfParts(template.getNumberOfParts());
        this.setUser(template.user);
        this.placeholders = new HashSet<>();
        this.pictures = new HashSet<>();
        this.paragraphs = new HashSet<>();
        this.partGroups = new HashSet<>();
        for (PartGroup partGroup:template.getPartGroups()){
            addPartGroup(new PartGroup(partGroup));
        }
        for (Paragraph p:template.getParagraphs()) {
            addParagraph(new Paragraph(p),p);
        }
        for (Picture p:template.getPictures()) {
            addPicture(new Picture(p),p);
        }
        for (Placeholder p:template.getPlaceholders()) {
            addPlaceholder(new Placeholder(p));
        }
    }

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

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "template")
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

    public void addParagraph(Paragraph paragraph, Paragraph p){
        paragraph.setTemplate(this);
        for (PartGroup partGroup : partGroups) {
            if(partGroup.getName().equals(p.getPartGroup().getName()))
                paragraph.setPartGroup(partGroup);
        }
        this.paragraphs.add(paragraph);
    }

    public void addPicture(Picture picture, Picture p){
        picture.setTemplate(this);
        for (PartGroup partGroup : partGroups) {
            if(partGroup.getName().equals(p.getPartGroup().getName()))
                picture.setPartGroup(partGroup);
        }
        this.pictures.add(picture);
    }

    public void addPlaceholder(Placeholder placeholder){
        placeholder.setTemplate(this);
        this.placeholders.add(placeholder);
    }

    public void addPartGroup(PartGroup partGroup){
        partGroup.setTemplate(this);
        this.partGroups.add(partGroup);
    }

}
