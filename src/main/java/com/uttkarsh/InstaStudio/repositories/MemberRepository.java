package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberProfile, Long> {
}
