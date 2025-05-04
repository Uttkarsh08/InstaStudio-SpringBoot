package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
        SELECT new com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO(
            e.clientName,
            e.eventStartDate,
            e.eventCity,
            e.evenIsSaved
        )
        FROM Event e
        WHERE e.studio.studioId = :studioId AND e.parentEvent IS NULL
    """)
    Page<EventListResponseDTO> findAllByStudio_StudioIdAndParentEventIsNull(@Param("studioId") Long studioId, Pageable pageable);
}
