package com.uttkarsh.InstaStudio.utils.mappers.Member;

import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Rating;
import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberReviewMapper {

    public MemberReviewResponseDTO toMemberReviewDTO(Rating rating) {
        if (rating == null) return null;

        MemberReviewResponseDTO dto = new MemberReviewResponseDTO();
        dto.setRatingId(rating.getRatingId());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setReviewBy(rating.getReviewBy());
        dto.setReview(rating.getReview());
        dto.setRatingValue(rating.getRatingValue());

        return dto;
    }
}
