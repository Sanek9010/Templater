package com.templater.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class TableStyle implements StyleInterface {

    private Long id;
    private String name;
    private Set<DocTable> docTables;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "tableStyle")
    public Set<DocTable> getDocTables() {
        return docTables;
    }

    public void setDocTables(Set<DocTable> docTables) {
        this.docTables = docTables;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    @Override
    public String getType() {
        return "Таблица";
    }
}
