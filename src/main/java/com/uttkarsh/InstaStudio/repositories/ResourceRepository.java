package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Page<Resource> findAllByStudio_StudioId(Long studioId, Pageable pageable);

    @Query("SELECT r.studio.studioId FROM Resource r WHERE r.resourceId = :id")
    Optional<Long> findStudioIdByResourceId(@Param("id") Long id);

    Set<Resource> findAllByResourceIdInAndStudio_StudioId(Set<Long> resourceIds, Long studioId);
}
