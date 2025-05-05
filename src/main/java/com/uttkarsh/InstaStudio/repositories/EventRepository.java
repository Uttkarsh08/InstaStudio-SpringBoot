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
    
    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateAfterOrderByEventStartDate(Long studioId, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateBeforeOrderByEventStartDateDesc(Long studioId, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullOrderByEventStartDate(Long studioId, Pageable pageable);

    List<Event> findAllByEventIdInOrderByEventStartDateAsc(Set<Long> subEventsIds);

    List<Event> findAllByStudio_StudioIdAndParentEventIsNullAndEvenIsSavedFalseAndEventStartDateAfterOrderByEventStartDate(Long studioId, LocalDateTime now);

    Optional<Event> findByEventIdAndStudio_StudioIdAndParentEventIsNull(Long eventId, Long studioId);

    Optional<Event> findFirstByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateAfterOrderByEventStartDate(Long studioId, LocalDateTime now);

    Optional<Event> findByEventIdAndStudio_StudioIdAndParentEventIsNotNull(Long eventId, Long studioId);

    Optional<Event> findByEventIdAndStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNull(Long eventId, Long studioId);

    List<Event> findAllByParentEventIsNullAndClientNameIsNull();
}
