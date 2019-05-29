package com.templater.repositories;

import com.templater.domain.Paragraph;
import com.templater.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByTemplate(Template template);

}
