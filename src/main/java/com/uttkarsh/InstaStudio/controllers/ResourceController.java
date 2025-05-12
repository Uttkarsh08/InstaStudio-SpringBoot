package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.services.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class ResourceController {

    @Value("${PAGE_SIZE}")
    private int PAGE_SIZE;


    private final ResourceService resourceService;

    @PostMapping("register/resource")
    public ResponseEntity<ResourceResponseDTO> createResource(
            @RequestBody ResourceRequestDTO requestDTO
    ){
        ResourceResponseDTO responseDTO = resourceService.createResource(requestDTO);
        return ResponseEntity.ok(responseDTO);

    }

    @GetMapping("{studioId}/resource/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> getResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId
    ){
        ResourceResponseDTO responseDTO = resourceService.getResourceById(studioId, resourceId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("{studioId}/all-resources")
    public ResponseEntity<Page<ResourceResponseDTO>> getAllMembers(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(resourceService.getAllResourcesForStudio(studioId, pageable));

    }

    @PutMapping("{studioId}/edit-resource/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> updateResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId,
            @RequestBody @Valid ResourceRequestDTO requestDTO
    ){
        ResourceResponseDTO resource = resourceService.updateResourceById(studioId, resourceId, requestDTO);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("{studioId}/delete-resource/{resourceId}")
    public ResponseEntity<Void> deleteResourceById(
            @PathVariable Long studioId,
            @PathVariable Long resourceId
    ){

        resourceService.deleteResourceById(studioId, resourceId);
        return ResponseEntity.noContent().build();
    }



}
