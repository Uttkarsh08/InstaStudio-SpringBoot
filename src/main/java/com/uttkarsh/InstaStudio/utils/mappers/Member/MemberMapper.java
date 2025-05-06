package com.uttkarsh.InstaStudio.utils.mappers.Member;

import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberMapper {

    public MemberResponseDTO toMemberDTO(User user) {
        if (user == null) return null;

        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setMemberId(user.getUserId());
        dto.setMemberName(user.getUserName());
        dto.setMemberEmail(user.getUserEmail());
        dto.setMemberPhoneNo(user.getUserPhoneNo());

        if (user.getMemberProfile() != null) {
            dto.setSalary(user.getMemberProfile().getMemberSalary());
            dto.setMemberAverageRating(user.getMemberProfile().getAverageRating());
            dto.setSpecialization(user.getMemberProfile().getSpecialization());
        }

        return dto;
    }
}