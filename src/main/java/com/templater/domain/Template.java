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
    private Set<User> users = new HashSet<>();
    private Set<Document> documents = new HashSet<>();
    private Set<Placeholder> placeholders = new HashSet<>();
    private Set<PartGroup> partGroups = new HashSet<>();

    public Template(){}

    public Template(Template template){
        this.setPrivateTemplate(template.getPrivateTemplate());
        this.setName(template.getName());
        this.setNumberOfParts(template.getNumberOfParts());
        this.setUsers(template.users);
        this.placeholders = new HashSet<>();
        this.partGroups = new HashSet<>();
        for (PartGroup partGroup:template.getPartGroups()){
            addPartGroup(new PartGroup(partGroup));
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

    @ManyToMany
    @JoinTable(
            name = "users_template",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id"))
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

    public void addPlaceholder(Placeholder placeholder){
        placeholder.setTemplate(this);
        this.placeholders.add(placeholder);
    }

    public void addPartGroup(PartGroup partGroup){
        partGroup.setTemplate(this);
        this.partGroups.add(partGroup);
    }

}
