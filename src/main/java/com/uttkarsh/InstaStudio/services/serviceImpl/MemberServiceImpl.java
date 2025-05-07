package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Rating;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.EventNotAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.MemberRepository;
import com.uttkarsh.InstaStudio.repositories.RatingRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.MemberService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final MemberMapper memberMapper;
    private final MemberReviewMapper memberReviewMapper;

    @Override
    public MemberResponseDTO createMember(MemberRequestDTO requestDTO) {
        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(()-> new ResourceNotFoundException("Studio can't be found with id:" + requestDTO.getStudioId()));

        User member = userRepository.getUserByUserEmail(requestDTO.getMemberEmail())
                .orElseThrow(()-> new ResourceNotFoundException("Cant find User with email:"+ requestDTO.getMemberEmail()));

        if (member.getUserType() != UserType.MEMBER) {
            throw new ResourceNotFoundException("User is not registered as a member.");
        }
        if (member.getStudio() != null) {
            throw new EventAlreadyAssignedException("Member already assigned to a studio.");
        }
        if (member.getMemberProfile() != null) {
            throw new IllegalStateException("MemberProfile already exists for this user.");
        }

        MemberProfile newMember = MemberProfile.builder()
                .memberSalary(requestDTO.getSalary())
                .specialization(requestDTO.getSpecialization())
                .user(member)
                .build();
        member.setStudio(studio);
        member.setMemberProfile(newMember);

        userRepository.save(member);

        return memberMapper.toMemberDTO(member);

    }

    @Override
    public MemberResponseDTO getMemberById(Long studioId, Long memberId) {

        User member = userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        MemberProfile profile = member.getMemberProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Member profile not found for member ID: " + memberId);
        }


        return memberMapper.toMemberDTO(member);
    }

    @Override
    public Page<MemberResponseDTO> getAllMemebersForStudio(Long studioId, Pageable pageable) {
        Page<User> members = userRepository.findAllByStudio_StudioIdAndUserType(studioId, UserType.MEMBER, pageable);

        return members.map(memberMapper::toMemberDTO);
    }

    @Override
    public MemberResponseDTO updateMemberById(Long studioId, Long memberId, MemberRequestDTO requestDTO) {
        User member = userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        if (!member.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Member doesn't belong to this studio");
        }

        member.getMemberProfile().setMemberSalary(requestDTO.getSalary());
        member.getMemberProfile().setSpecialization(requestDTO.getSpecialization());

        User updatedMember = userRepository.save(member);
        return memberMapper.toMemberDTO(updatedMember);

    }

    @Override
    public void deleteMemberById(Long studioId, Long memberId) {
        User member = userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        if (!member.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event does not belong to the specified studio");
        }

        MemberProfile memberProfile = member.getMemberProfile();
        if (memberProfile == null) {
            throw new ResourceNotFoundException("Member profile not found for member ID: " + memberId);
        }
        member.setMemberProfile(null);
        userRepository.save(member);

    }

    @Override
    public Page<MemberReviewResponseDTO> getMemberReviewsById(Long studioId, Long memberId, Pageable pageable) {

        userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        Page<Rating> memberProfile = ratingRepository.findAllByMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(memberId, studioId, pageable);

        return memberProfile.map(memberReviewMapper::toMemberReviewDTO);
    }
}
