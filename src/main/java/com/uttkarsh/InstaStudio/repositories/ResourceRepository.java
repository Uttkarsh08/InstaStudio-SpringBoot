package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Resource;
import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Page<Resource> findAllByStudio_StudioId(Long studioId, Pageable pageable);

    @Query("SELECT r.studio.studioId FROM Resource r WHERE r.resourceId = :id")
    Optional<Long> findStudioIdByResourceId(@Param("id") Long id);

    Set<Resource> findAllByResourceIdInAndStudio_StudioId(Set<Long> resourceIds, Long studioId);

    @Query("""
    SELECT r FROM Resource r
    WHERE r.studio.studioId = :studioId
    AND r NOT IN (
        SELECT res FROM Event e
        JOIN e.resources res
        WHERE (
            (e.eventStartDate <= :endDate AND e.eventEndDate >= :startDate)
        )
    )
""")
    List<Resource> findAvailableResourcesByStudioAndDateRange(
            @Param("studioId") Long studioId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT r FROM Resource r
    WHERE r.studio.studioId = :studioId AND (
        LOWER(r.resourceName) LIKE LOWER(CONCAT('%', :query, '%')) OR
        STR(r.resourcePrice) LIKE CONCAT('%', :query, '%')
    )
""")
    Page<Resource> searchAllResources(Long studioId, String query, Pageable pageable);
}
