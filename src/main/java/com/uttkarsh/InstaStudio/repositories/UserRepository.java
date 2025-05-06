package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByFirebaseId(String firebaseId);

    boolean existsByFirebaseId(String firebaseId);

    Optional<User> getUserByUserEmail(String memberEmail);

    Optional<User> findByUserIdAndStudio_StudioIdAndUserType(Long memberId, Long studioId, UserType userType);

    Page<User> findAllByStudio_StudioIdAndUserType(Long studioId, UserType userType, Pageable pageable);


    Optional<User> findByUserIdAndUserType(Long userId, UserType userType);
}
