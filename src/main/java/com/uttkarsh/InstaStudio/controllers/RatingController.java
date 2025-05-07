package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("{studioId}/member/{memberId}/register/review")
    public ResponseEntity<MemberReviewResponseDTO> createReviewForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestBody @Valid MemberReviewRequestDTO requestDTO
    ){
        MemberReviewResponseDTO responseDTO = ratingService.createReview(studioId, memberId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("{studioId}/member/{memberId}/review/{reviewId}")
    public ResponseEntity<MemberReviewResponseDTO> getReviewById(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @PathVariable Long reviewId
    ){
        MemberReviewResponseDTO responseDTO = ratingService.getReviewById(studioId, memberId, reviewId);
        return ResponseEntity.ok(responseDTO);
    }

}
