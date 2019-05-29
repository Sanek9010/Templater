package com.templater.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Template {
    private Long id;
    private String name;
    private String dateOfCreation;
    private Long numberOfParts;
    private Set<User> users;
    private Set<Paragraph> paragraphs;
    private Set<DocTable> docTables;
    private Set<Picture> pictures;
    private Set<Document> documents;
    private Set<Placeholder> placeholders;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Set<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<DocTable> getDocTables() {
        return docTables;
    }

    public void setDocTables(Set<DocTable> docTables) {
        this.docTables = docTables;
    }
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "template")
    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }

    @ManyToMany(mappedBy = "templates")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
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

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Long getNumberOfParts() {
        return numberOfParts;
    }

    public void setNumberOfParts(Long numberOfParts) {
        this.numberOfParts = numberOfParts;
    }

}
