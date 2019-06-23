package com.templater.repositories;

import com.templater.domain.PartGroup;
import com.templater.domain.TableStyle;
import com.templater.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartGroupRepository extends JpaRepository<PartGroup, Long> {

    List<PartGroup> findByTemplate(Template template);

    Optional<PartGroup> findByName(String name);
}
