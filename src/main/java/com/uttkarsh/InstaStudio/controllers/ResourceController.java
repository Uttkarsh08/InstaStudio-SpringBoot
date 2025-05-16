package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.services.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class ResourceController {

    @Value("${PAGE_SIZE}")
    private int PAGE_SIZE;

//    private final int PAGE_SIZE = 5;  //for unit testing


    private final ResourceService resourceService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("register/resource")
    public ResponseEntity<ResourceResponseDTO> createResource(
            @Valid @RequestBody ResourceRequestDTO requestDTO
    ){
        ResourceResponseDTO responseDTO = resourceService.createResource(requestDTO);
        return ResponseEntity.ok(responseDTO);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{studioId}/resource/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> getResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId
    ){
        ResourceResponseDTO responseDTO = resourceService.getResourceById(studioId, resourceId);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{studioId}/all-resources")
    public ResponseEntity<Page<ResourceResponseDTO>> getAllResources(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(resourceService.getAllResourcesForStudio(studioId, pageable));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{studioId}/edit-resource/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> updateResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId,
            @RequestBody @Valid ResourceRequestDTO requestDTO
    ){
        ResourceResponseDTO resource = resourceService.updateResourceById(studioId, resourceId, requestDTO);
        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{studioId}/delete-resource/{resourceId}")
    public ResponseEntity<Void> deleteResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId
    ){

        resourceService.deleteResourceById(studioId, resourceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studioId}/available-resources")
    public ResponseEntity<List<ResourceResponseDTO>> getAvailableResources(
            @PathVariable Long studioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(resourceService.getALlAvailableResources(studioId, startDate, endDate));
    }

    //SEARCHING

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studioId}/search/resources")
    public ResponseEntity<Page<ResourceResponseDTO>> searchAllResources(
            @PathVariable Long studioId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(resourceService.searchAllResources(studioId, query, pageable));
    }



}
