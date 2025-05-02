package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByFirebaseIdAndUserType(String firebaseId, UserType type);

    boolean existsByFirebaseIdAndUserType(String firebaseId, UserType userType);
}
