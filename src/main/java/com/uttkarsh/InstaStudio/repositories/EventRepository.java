package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullAndEventStartDateAfterOrderByEventStartDate(Long studioId, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullAndEventStartDateBeforeOrderByEventStartDate(Long studioId, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullOrderByEventStartDate(Long studioId, Pageable pageable);

    List<Event> findAllByEventIdInOrderByEventStartDateAsc(Set<Long> subEventsIds);

    List<Event> findAllByStudio_StudioIdAndParentEventIsNullAndEvenIsSavedFalseAndEventStartDateAfterOrderByEventStartDate(Long studioId, LocalDateTime now);

    Optional<Event> findByEventIdAndStudio_StudioId(Long studioId, Long eventId);
}
