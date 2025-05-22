package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
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
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private StudioRepository studioRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper memberMapper;
    @Mock
    private MemberReviewMapper memberReviewMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Studio studio;
    private User memberUser;
    private MemberProfile memberProfile;
    private MemberRequestDTO memberRequestDTO;
    private MemberResponseDTO memberResponseDTO;

    @BeforeEach
    void setUp() {
        studio = new Studio();
        studio.setStudioId(1L);

        memberProfile = MemberProfile.builder()
                .memberId(10L)
                .memberSalary(50000L)
                .specialization("Photography")
                .build();

        memberProfile.setEvents(new LinkedHashSet<>());
        memberProfile.setRatings(new LinkedList<>());

        memberUser = new User();
        memberUser.setUserId(10L);
        memberUser.setUserEmail("member@test.com");
        memberUser.setUserType(UserType.MEMBER);
        memberUser.setStudio(null);
        memberUser.setMemberProfile(null);

        memberRequestDTO = new MemberRequestDTO();
        memberRequestDTO.setStudioId(1L);
        memberRequestDTO.setMemberEmail("member@test.com");
        memberRequestDTO.setSalary(50000L);
        memberRequestDTO.setSpecialization("Photography");

        memberResponseDTO = new MemberResponseDTO();
        memberResponseDTO.setMemberId(10L);
        memberResponseDTO.setMemberEmail("member@test.com");

    }

    // ------ createMember ------

    @Test
    void createMember_Success() {
        when(studioRepository.findById(memberRequestDTO.getStudioId())).thenReturn(Optional.of(studio));
        when(userRepository.getUserByUserEmail(memberRequestDTO.getMemberEmail())).thenReturn(Optional.of(memberUser));

        // Member user is correct type, no studio, no profile
        memberUser.setUserType(UserType.MEMBER);
        memberUser.setStudio(null);
        memberUser.setMemberProfile(null);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(memberMapper.toMemberDTO(any(User.class))).thenReturn(memberResponseDTO);

        MemberResponseDTO result = memberService.createMember(memberRequestDTO);

        assertNotNull(result);
        assertEquals(memberResponseDTO.getMemberEmail(), result.getMemberEmail());
        verify(userRepository).save(memberUser);
    }

    @Test
    void createMember_StudioNotFound_ThrowsResourceNotFoundException() {
        when(studioRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.createMember(memberRequestDTO));
        assertTrue(ex.getMessage().contains("Studio can't be found"));
    }

    @Test
    void createMember_UserEmailNotFound_ThrowsResourceNotFoundException() {
        when(studioRepository.findById(anyLong())).thenReturn(Optional.of(studio));
        when(userRepository.getUserByUserEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.createMember(memberRequestDTO));
        assertTrue(ex.getMessage().contains("Cant find User"));
    }

    @Test
    void createMember_UserNotMember_ThrowsResourceNotFoundException() {
        when(studioRepository.findById(anyLong())).thenReturn(Optional.of(studio));
        memberUser.setUserType(UserType.CUSTOMER);
        when(userRepository.getUserByUserEmail(anyString())).thenReturn(Optional.of(memberUser));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.createMember(memberRequestDTO));
        assertTrue(ex.getMessage().contains("User is not registered as a member"));
    }

    @Test
    void createMember_MemberAlreadyHasStudio_ThrowsEventAlreadyAssignedException() {
        when(studioRepository.findById(anyLong())).thenReturn(Optional.of(studio));
        memberUser.setUserType(UserType.MEMBER);
        memberUser.setStudio(new Studio());
        when(userRepository.getUserByUserEmail(anyString())).thenReturn(Optional.of(memberUser));

        EventAlreadyAssignedException ex = assertThrows(EventAlreadyAssignedException.class, () ->
                memberService.createMember(memberRequestDTO));
        assertTrue(ex.getMessage().contains("Member already assigned to a studio"));
    }

    @Test
    void createMember_MemberProfileAlreadyExists_ThrowsIllegalStateException() {
        when(studioRepository.findById(anyLong())).thenReturn(Optional.of(studio));
        memberUser.setUserType(UserType.MEMBER);
        memberUser.setStudio(null);
        memberUser.setMemberProfile(new MemberProfile());
        when(userRepository.getUserByUserEmail(anyString())).thenReturn(Optional.of(memberUser));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                memberService.createMember(memberRequestDTO));
        assertTrue(ex.getMessage().contains("MemberProfile already exists"));
    }

    // ------ getMemberById ------

    @Test
    void getMemberById_Success() {
        doNothing().when(validationService).isStudioValid(anyLong());
        memberUser.setMemberProfile(memberProfile);
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.of(memberUser));
        when(memberMapper.toMemberDTO(any(User.class))).thenReturn(memberResponseDTO);

        MemberResponseDTO result = memberService.getMemberById(1L, 10L);
        assertNotNull(result);
        assertEquals(memberResponseDTO.getMemberId(), result.getMemberId());
    }

    @Test
    void getMemberById_MemberNotFound_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(anyLong());
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.getMemberById(1L, 10L));
        assertTrue(ex.getMessage().contains("Member can't be found"));
    }

    @Test
    void getMemberById_MemberProfileNull_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(anyLong());
        memberUser.setMemberProfile(null);
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.of(memberUser));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.getMemberById(1L, 10L));
        assertTrue(ex.getMessage().contains("Member profile not found"));
    }

    // ------ getAllMemebersForStudio ------

    @Test
    void getAllMemebersForStudio_Success() {
        doNothing().when(validationService).isStudioValid(anyLong());

        Page<User> usersPage = new PageImpl<>(List.of(memberUser));
        when(userRepository.findAllByStudio_StudioIdAndUserType(anyLong(), eq(UserType.MEMBER), any(Pageable.class)))
                .thenReturn(usersPage);
        when(memberMapper.toMemberDTO(any(User.class))).thenReturn(memberResponseDTO);

        Page<MemberResponseDTO> result = memberService.getAllMemebersForStudio(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAllByStudio_StudioIdAndUserType(1L, UserType.MEMBER, PageRequest.of(0, 10));
    }

    // ------ updateMemberById ------

    @Test
    void updateMemberById_Success() {
        doNothing().when(validationService).isStudioValid(anyLong());
        when(userRepository.findStudioIdByMemberId(anyLong())).thenReturn(Optional.of(1L));
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.of(memberUser));

        memberUser.setMemberProfile(memberProfile);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(memberMapper.toMemberDTO(any(User.class))).thenReturn(memberResponseDTO);

        MemberResponseDTO result = memberService.updateMemberById(1L, 10L, memberRequestDTO);

        assertNotNull(result);
        verify(userRepository).save(memberUser);
    }

    @Test
    void updateMemberById_StudioMismatch_ThrowsEventNotAssignedException() {
        Long inputStudioId = 1L;
        Long associatedStudioId = 2L;
        Long memberId = 10L;

        MemberRequestDTO memberRequestDTO = mock(MemberRequestDTO.class);

        doNothing().when(validationService).isStudioValid(inputStudioId);
        when(userRepository.findStudioIdByMemberId(memberId)).thenReturn(Optional.of(associatedStudioId));

        EventNotAssignedException ex = assertThrows(EventNotAssignedException.class, () ->
                memberService.updateMemberById(inputStudioId, memberId, memberRequestDTO));

        assertTrue(ex.getMessage().contains("Member doesn't belong to this studio"));
    }

    @Test
    void updateMemberById_MemberNotFound_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(anyLong());
        when(userRepository.findStudioIdByMemberId(anyLong())).thenReturn(Optional.of(1L));
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.updateMemberById(1L, 10L, memberRequestDTO));
        assertTrue(ex.getMessage().contains("Member can't be found"));
    }

    // ------ deleteMemberById ------

    @Test
    void deleteMemberById_Success() {
        Long studioId = 1L;
        Long memberId = 10L;

        Studio studio = new Studio();
        studio.setStudioId(studioId);

        MemberProfile memberProfile = new MemberProfile();
        memberProfile.setEvents(new LinkedHashSet<>());

        User member = new User();
        member.setUserId(memberId);
        member.setStudio(studio);
        member.setMemberProfile(memberProfile);

        doNothing().when(validationService).isStudioValid(studioId);
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, studioId, UserType.MEMBER))
                .thenReturn(Optional.of(member));
        when(userRepository.save(any(User.class))).thenReturn(member);

        doNothing().when(memberRepository).delete(memberProfile);
        memberService.deleteMemberById(studioId, memberId);

        verify(userRepository).save(member);
        verify(memberRepository).delete(memberProfile);
    }




    @Test
    void deleteMemberById_StudioMismatch_ThrowsEventNotAssignedException() {
        Long providedStudioId = 1L;
        Long actualStudioId = 2L;
        Long memberId = 10L;

        Studio memberStudio = new Studio();
        memberStudio.setStudioId(actualStudioId);

        User member = new User();
        member.setUserId(memberId);
        member.setStudio(memberStudio);
        member.setUserType(UserType.MEMBER);

        doNothing().when(validationService).isStudioValid(providedStudioId);
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(memberId, providedStudioId, UserType.MEMBER))
                .thenReturn(Optional.of(member));

        EventNotAssignedException ex = assertThrows(EventNotAssignedException.class, () ->
                memberService.deleteMemberById(providedStudioId, memberId));

        assertTrue(ex.getMessage().contains("Event does not belong to the specified studio"));
    }

    @Test
    void deleteMemberById_MemberNotFound_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(anyLong());
        when(userRepository.findByUserIdAndStudio_StudioIdAndUserType(anyLong(), anyLong(), eq(UserType.MEMBER)))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.deleteMemberById(1L, 10L));
        assertTrue(ex.getMessage().contains("Member can't be found"));
    }
}