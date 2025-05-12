package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.entities.*;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.EventNotAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.*;
import com.uttkarsh.InstaStudio.services.MemberService;
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ValidationService validationService;
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MemberReviewMapper memberReviewMapper;
    private final EventRepository eventRepository;

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
        validationService.isStudioValid(studioId);

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
        validationService.isStudioValid(studioId);

        Page<User> members = userRepository.findAllByStudio_StudioIdAndUserType(studioId, UserType.MEMBER, pageable);

        return members.map(memberMapper::toMemberDTO);
    }

    @Override
    public MemberResponseDTO updateMemberById(Long studioId, Long memberId, MemberRequestDTO requestDTO) {
        validationService.isStudioValid(studioId);

        Long associatedStudioId = userRepository.findStudioIdByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: " + studioId));

        if (!associatedStudioId.equals(studioId) || !associatedStudioId.equals(requestDTO.getStudioId())) {
            throw new EventNotAssignedException("Member doesn't belong to this studio");
        }

        User member = userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));


        member.getMemberProfile().setMemberSalary(requestDTO.getSalary());
        member.getMemberProfile().setSpecialization(requestDTO.getSpecialization());

        User updatedMember = userRepository.save(member);
        return memberMapper.toMemberDTO(updatedMember);

    }

    @Transactional
    @Override
    public void deleteMemberById(Long studioId, Long memberId) {
        validationService.isStudioValid(studioId);

        User member = userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        if (!member.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event does not belong to the specified studio");
        }

        MemberProfile memberProfile = member.getMemberProfile();
        if (memberProfile == null) {
            throw new ResourceNotFoundException("Member profile not found for member ID: " + memberId);
        }

        for (Event event : new LinkedHashSet<>(memberProfile.getEvents())) {
            event.getMembers().remove(memberProfile);
        }
        memberProfile.getEvents().clear();

        member.setStudio(null);
        member.setMemberProfile(null);
        userRepository.save(member);

        memberRepository.delete(memberProfile);

    }

    @Override
    public Page<MemberReviewResponseDTO> getMemberReviewsById(Long studioId, Long memberId, Pageable pageable) {
        validationService.isStudioValid(studioId);

        userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member can't be found with id: "+  memberId));

        Page<Rating> memberProfile = ratingRepository.findAllByMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(memberId, studioId, pageable);

        return memberProfile.map(memberReviewMapper::toMemberReviewDTO);
    }

    @Override
    public Page<MemberResponseDTO> searchAllMembers(Long studioId, String query, Pageable pageable) {
        validationService.isStudioValid(studioId);

        Page<User> members = userRepository.searchAllMembers(studioId, query, pageable);
        return members.map(memberMapper::toMemberDTO);
    }

    @Override
    public List<MemberResponseDTO> getALlAvailableMembers(Long studioId, LocalDateTime startDate, LocalDateTime endDate) {
        validationService.isStudioValid(studioId);

        List<User> availableMembers = userRepository.findAvailableMembersByStudioAndDateRange(studioId, startDate, endDate);
        return availableMembers.stream().map(memberMapper::toMemberDTO).toList();
    }
}
