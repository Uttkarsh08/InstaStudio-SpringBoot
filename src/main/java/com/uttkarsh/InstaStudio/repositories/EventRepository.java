package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Event;
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

    Page<Event> findByMembers_MemberIdAndStudio_StudioIdAndParentEventIsNotNullOrderByEventStartDate(Long memberId, Long studioId, Pageable pageable);

    Optional<Event> findFirstByStudio_StudioIdAndMembers_MemberIdAndAndParentEventIsNotNullAndEventStartDateAfterOrderByEventStartDate(Long studioId, Long memberId, LocalDateTime now);

    Page<Event> findAllByStudio_StudioIdAndMembers_MemberIdAndParentEventIsNotNullAndEventStartDateAfterOrderByEventStartDate(Long studioId, Long memberId, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByStudio_StudioIdAndMembers_MemberIdAndParentEventIsNotNullAndEventStartDateBeforeOrderByEventStartDateDesc(Long studioId, Long memberId, LocalDateTime now, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.studio.studioId = :studioId AND " +

            "(LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Event> searchAllEvents(@Param("studioId") Long studioId,
                                @Param("query") String query,
                                Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.studio.studioId = :studioId AND e.eventStartDate > :now AND " +
            "(LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Event> searchUpcomingEvents(@Param("studioId") Long studioId,
                                     @Param("now") LocalDateTime now,
                                     @Param("query") String query,
                                     Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.studio.studioId = :studioId AND e.eventEndDate < :now AND " +
            "(LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY e.eventEndDate DESC")
    Page<Event> searchCompletedEvents(@Param("studioId") Long studioId,
                                      @Param("now") LocalDateTime now,
                                      @Param("query") String query,
                                      Pageable pageable);


    //Member's Events Search

    @Query("SELECT e FROM Event e " +
            "JOIN e.members m " +
            "WHERE e.studio.studioId = :studioId " +
            "AND m.memberId = :memberId " +
            "AND e.parentEvent IS NOT NULL " +
            "AND e.clientName IS NOT NULL " +
            "AND (LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY e.eventStartDate")
    Page<Event> searchAllEventsForMember(@Param("studioId") Long studioId,
                                         @Param("memberId") Long memberId,
                                         @Param("query") String query,
                                         Pageable pageable);


    @Query("SELECT e FROM Event e " +
            "JOIN e.members m " +
            "WHERE e.studio.studioId = :studioId " +
            "AND m.memberId = :memberId " +
            "AND e.parentEvent IS NOT NULL " +
            "AND e.clientName IS NOT NULL " +
            "AND e.eventStartDate > :now " +
            "AND (LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY e.eventStartDate")
    Page<Event> searchUpcomingEventsByMemberAndStudio(
            @Param("studioId") Long studioId,
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now,
            @Param("query") String query,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "JOIN e.members m " +
            "WHERE e.studio.studioId = :studioId " +
            "AND m.memberId = :memberId " +
            "AND e.parentEvent IS NOT NULL " +
            "AND e.clientName IS NOT NULL " +
            "AND e.eventEndDate < :now " +
            "AND (LOWER(e.clientName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.clientPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventType) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.eventCity) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY e.eventStartDate DESC")
    Page<Event> searchCompletedEventsByMemberAndStudio(
            @Param("studioId") Long studioId,
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now,
            @Param("query") String query,
            Pageable pageable);

    Event findFirstByResources_ResourceIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateBeforeOrderByEventStartDateDesc(Long resourceId, LocalDateTime now);
}
