package com.uttkarsh.InstaStudio.repositories;

import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Studio;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioRepository extends JpaRepository<Studio, Long> {

}
