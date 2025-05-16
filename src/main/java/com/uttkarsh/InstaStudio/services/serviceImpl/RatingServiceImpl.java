package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Rating;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.MemberRepository;
import com.uttkarsh.InstaStudio.repositories.RatingRepository;
import com.uttkarsh.InstaStudio.services.RatingService;
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;
    private final MemberReviewMapper memberReviewMapper;
    private final ValidationService validationService;

    @Override
    public MemberReviewResponseDTO createReview(Long studioId, Long memberId, MemberReviewRequestDTO requestDTO) {
        validationService.isStudioValid(studioId);

        MemberProfile member = memberRepository.findByMemberIdAndUser_Studio_StudioId(memberId, studioId)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        Rating newRating = new Rating();
        newRating.setReviewBy(requestDTO.getReviewBy());
        newRating.setReview(requestDTO.getReview());
        newRating.setRatingValue(requestDTO.getRatingValue());
        newRating.setMemberProfile(member);

        Rating savedRating = ratingRepository.save(newRating);
        return memberReviewMapper.toMemberReviewDTO(savedRating);
    }

    @Override
    public MemberReviewResponseDTO getReviewById(Long studioId, Long memberId, Long reviewId) {
        validationService.isStudioValid(studioId);
        validationService.isMemberValid(studioId, memberId);

        Rating review = ratingRepository.findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId)
                .orElseThrow(() -> new ResourceNotFoundException("Review can't be found with id: "+  reviewId));

        return memberReviewMapper.toMemberReviewDTO(review);
    }

}
