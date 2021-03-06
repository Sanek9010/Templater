package com.templater.repositories;

import com.templater.domain.Placeholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceholderRepository extends JpaRepository<Placeholder, Long> {
}
