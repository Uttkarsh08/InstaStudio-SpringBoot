package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface RatingService {

    MemberReviewResponseDTO createReview(Long studioId, Long memberId, @Valid MemberReviewRequestDTO requestDTO);

    MemberReviewResponseDTO getReviewById(Long studioId, Long memberId, Long reviewId);


}
