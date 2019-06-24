package com.templater.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PartGroup {
    private Long id;
    private String name;
    private Template template;
    private Set<Paragraph> paragraphs = new HashSet<>();
    private Set<Picture> pictures = new HashSet<>();

    public PartGroup() {}

    public PartGroup(PartGroup partGroup) {
        this.name = partGroup.getName();
        this.pictures = new HashSet<>();
        this.paragraphs = new HashSet<>();
        for (Paragraph p:partGroup.getParagraphs()) {
            addParagraph(new Paragraph(p));
        }
        for (Picture p:partGroup.getPictures()) {
            addPicture(new Picture(p));
        }
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
    public Set<Picture> getPictures() {
        return pictures;
    }
    public void setPictures(Set<Picture> pictures) {

        this.pictures=pictures;

    }

    public void addParagraph(Paragraph paragraph){
        this.paragraphs.add(paragraph);
        paragraph.setPartGroup(this);
    }

    public void addPicture(Picture picture){
        this.pictures.add(picture);
        picture.setPartGroup(this);
    }
}
