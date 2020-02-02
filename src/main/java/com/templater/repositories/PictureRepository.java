package com.templater.repositories;

import com.templater.domain.PartGroup;
import com.templater.domain.Picture;
import com.templater.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByPartGroup(PartGroup partGroup);
}
