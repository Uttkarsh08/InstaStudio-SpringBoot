package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    MemberResponseDTO createMember(MemberRequestDTO requestDTO);

    MemberResponseDTO getMemberById(Long studioId, Long memberId);

    Page<MemberResponseDTO> getAllMemebersForStudio(Long studioId, Pageable pageable);

    MemberResponseDTO updateMemberById(Long studioId, Long memberId, MemberRequestDTO requestDTO);

    void deleteMemberById(Long studioId, Long memberId);

    Page<MemberReviewResponseDTO> getMemberReviewsById(Long studioId, Long memberId, Pageable pageable);

}
