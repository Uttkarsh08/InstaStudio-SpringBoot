package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.entities.Studio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioRepository extends JpaRepository<Studio, Long> {

}
