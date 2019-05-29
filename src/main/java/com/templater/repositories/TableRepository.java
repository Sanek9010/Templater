package com.templater.repositories;

import com.templater.domain.DocTable;
import com.templater.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<DocTable, Long> {
    List<DocTable> findByTemplate(Template template);
}
