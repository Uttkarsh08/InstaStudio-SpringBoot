package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.services.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class MemberController {

    @Value("${PAGE_SIZE}")
    private int PAGE_SIZE;


    private final MemberService memberService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("register/member")
    public ResponseEntity<MemberResponseDTO> createMember(
            @Valid @RequestBody MemberRequestDTO requestDTO
    ){
        MemberResponseDTO responseDTO = memberService.createMember(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("{studioId}/member/{memberId}")
    public ResponseEntity<MemberResponseDTO> getMemberById(
            @PathVariable Long studioId,
            @PathVariable Long memberId
    ){
        MemberResponseDTO member = memberService.getMemberById(studioId, memberId);
        return ResponseEntity.ok(member);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{studioId}/all-members")
    public ResponseEntity<Page<MemberResponseDTO>> getAllMembers(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(memberService.getAllMemebersForStudio(studioId, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{studioId}/edit-member/{memberId}")
    public ResponseEntity<MemberResponseDTO> updateMemberById(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestBody @Valid MemberRequestDTO responseDTO
    ){
        MemberResponseDTO member = memberService.updateMemberById(studioId, memberId, responseDTO);
        return ResponseEntity.ok(member);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{studioId}/delete-member/{memberId}")
    public ResponseEntity<Void> deleteMemberById(
            @PathVariable Long studioId,
            @PathVariable Long memberId
    ){

        memberService.deleteMemberById(studioId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'CUSTOMER')")
    @GetMapping("{studioId}/member/{memberId}/reviews")
    public ResponseEntity<Page<MemberReviewResponseDTO>> getMemberReviewsById(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(memberService.getMemberReviewsById(studioId, memberId, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studioId}/available-members")
    public ResponseEntity<List<MemberResponseDTO>> getAvailableMembers(
            @PathVariable Long studioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(memberService.getALlAvailableMembers(studioId, startDate, endDate));
    }

    //SEARCHING

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studioId}/search/members")
    public ResponseEntity<Page<MemberResponseDTO>> searchAllMembers(
            @PathVariable Long studioId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(memberService.searchAllMembers(studioId, query, pageable));
    }


}
