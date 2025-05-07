package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findAllByMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(Long memberId, Long studioId, Pageable pageable);

    Optional<Rating> findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(Long reviewId, Long memberId, Long studioId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.memberProfile.memberId = :memberId")
    Double findAverageRatingByMemberId(@Param("memberId") Long memberId);
}
