package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByFirebaseId(String firebaseId);

    boolean existsByFirebaseId(String firebaseId);

    Optional<User> getUserByUserEmail(String memberEmail);

    Optional<User> findByUserIdAndStudio_StudioIdAndUserType(Long memberId, Long studioId, UserType userType);

    Page<User> findAllByStudio_StudioIdAndUserType(Long studioId, UserType userType, Pageable pageable);


    Optional<User> findByUserIdAndUserType(Long userId, UserType userType);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.memberProfile mp " +
            "WHERE u.studio.studioId = :studioId AND u.userType = 'MEMBER' AND " +
            "(LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.userPhoneNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.userEmail) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(mp.specialization) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchAllMembers(@Param("studioId") Long studioId,
                                @Param("query") String query,
                                Pageable pageable);

    @Query("""
        SELECT u FROM User u
        WHERE u.studio.studioId = :studioId
        AND u.userType = 'MEMBER'
        AND u.memberProfile IS NOT NULL
        AND u.memberProfile NOT IN (
            SELECT m FROM Event e
            JOIN e.members m
            WHERE (
                (e.eventStartDate <= :endDate AND e.eventEndDate >= :startDate)
            )
        )
    """)
    List<User> findAvailableMembersByStudioAndDateRange(
            @Param("studioId") Long studioId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
