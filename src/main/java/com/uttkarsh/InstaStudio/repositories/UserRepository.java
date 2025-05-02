package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
