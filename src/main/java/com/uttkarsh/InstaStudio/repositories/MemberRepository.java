package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<MemberProfile, Long> {

    Set<MemberProfile> findAllByMemberIdInAndUser_Studio_StudioId(Set<Long> memberIds, Long studioId);

    Optional<MemberProfile> findByMemberIdAndUser_Studio_StudioId(Long memberId, Long studioId);
}
