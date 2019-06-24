package com.templater.repositories;

import com.templater.domain.Template;
import com.templater.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByPrivateTemplate(Boolean privateTemplate);
}
