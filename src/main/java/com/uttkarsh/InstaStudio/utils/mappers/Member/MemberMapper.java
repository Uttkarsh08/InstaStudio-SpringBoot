package com.uttkarsh.InstaStudio.utils.mappers.Member;

import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MemberMapper {

    private final RatingRepository ratingRepository;

    public MemberResponseDTO toMemberDTO(User user) {
        if (user == null) return null;

        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setMemberId(user.getUserId());
        dto.setMemberName(user.getUserName());
        dto.setMemberEmail(user.getUserEmail());
        dto.setMemberPhoneNo(user.getUserPhoneNo());

        if (user.getMemberProfile() != null) {
            dto.setSalary(user.getMemberProfile().getMemberSalary());
            dto.setSpecialization(user.getMemberProfile().getSpecialization());

            Double averageRating = ratingRepository.findAverageRatingByMemberId(user.getMemberProfile().getMemberId());
            dto.setMemberAverageRating(averageRating != null ? averageRating.longValue() : 0L);
        }

        return dto;
    }
}