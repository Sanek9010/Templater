package com.templater.repositories;

import com.templater.domain.ParagraphStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<ParagraphStyle, Long> {
}
