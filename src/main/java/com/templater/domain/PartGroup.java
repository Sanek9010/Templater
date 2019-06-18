package com.templater.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class PartGroup {
    private Long id;
    private String name;
    private Template template;
    private Set<Paragraph> paragraphs;
    private Set<DocTable> docTables;
    private Set<Picture> pictures;

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

    @ManyToOne
    @JoinColumn(name = "template_id")
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "partGroup")
    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }
    public void setParagraphs(Set<Paragraph> paragraphs) {

        this.paragraphs=paragraphs;

    }
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "partGroup")
    public Set<DocTable> getDocTables() {
        return docTables;
    }
    public void setDocTables(Set<DocTable> docTables) {

        this.docTables=docTables;

    }
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "partGroup")
    public Set<Picture> getPictures() {
        return pictures;
    }
    public void setPictures(Set<Picture> pictures) {

        this.pictures=pictures;

    }
}
