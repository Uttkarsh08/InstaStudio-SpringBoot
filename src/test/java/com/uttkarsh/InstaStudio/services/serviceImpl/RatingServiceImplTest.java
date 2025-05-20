package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Rating;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.MemberRepository;
import com.uttkarsh.InstaStudio.repositories.RatingRepository;
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberReviewMapper memberReviewMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private final Long studioId = 1L;
    private final Long memberId = 2L;
    private final Long reviewId = 3L;

    private MemberReviewRequestDTO validRequestDTO;
    private MemberProfile memberProfile;
    private Rating rating;
    private MemberReviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new MemberReviewRequestDTO();
        validRequestDTO.setReviewBy("John Doe");
        validRequestDTO.setReview("Excellent service");
        validRequestDTO.setRatingValue(4);

        memberProfile = new MemberProfile();
        memberProfile.setMemberId(memberId);

        rating = new Rating();
        rating.setRatingId(reviewId);
        rating.setReviewBy(validRequestDTO.getReviewBy());
        rating.setReview(validRequestDTO.getReview());
        rating.setRatingValue(validRequestDTO.getRatingValue());
        rating.setMemberProfile(memberProfile);

        responseDTO = new MemberReviewResponseDTO();
        responseDTO.setRatingId(reviewId);
        responseDTO.setReviewBy(validRequestDTO.getReviewBy());
        responseDTO.setReview(validRequestDTO.getReview());
        responseDTO.setRatingValue(validRequestDTO.getRatingValue());
    }

    @Test
    void createReview_WhenValidRequest_SavesAndReturnsDTO() {
        doNothing().when(validationService).isStudioValid(studioId);
        when(memberRepository.findByMemberIdAndUser_Studio_StudioId(memberId, studioId))
            .thenReturn(Optional.of(memberProfile));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(memberReviewMapper.toMemberReviewDTO(rating)).thenReturn(responseDTO);

        MemberReviewResponseDTO result = ratingService.createReview(studioId, memberId, validRequestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(validationService).isStudioValid(studioId);
        verify(memberRepository).findByMemberIdAndUser_Studio_StudioId(memberId, studioId);
        verify(ratingRepository).save(any(Rating.class));
        verify(memberReviewMapper).toMemberReviewDTO(rating);
    }

    @Test
    void createReview_WhenStudioInvalid_ThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Studio not found")).when(validationService).isStudioValid(studioId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> ratingService.createReview(studioId, memberId, validRequestDTO));

        assertEquals("Studio not found", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verifyNoInteractions(memberRepository, ratingRepository, memberReviewMapper);
    }

    @Test
    void createReview_WhenMemberNotFound_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(studioId);
        when(memberRepository.findByMemberIdAndUser_Studio_StudioId(memberId, studioId))
            .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> ratingService.createReview(studioId, memberId, validRequestDTO));

        assertEquals("Member can't be found with id: " + memberId, ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(memberRepository).findByMemberIdAndUser_Studio_StudioId(memberId, studioId);
        verifyNoInteractions(ratingRepository, memberReviewMapper);
    }

    @Test
    void createReview_WhenSaveFails_ThrowsException() {
        doNothing().when(validationService).isStudioValid(studioId);
        when(memberRepository.findByMemberIdAndUser_Studio_StudioId(memberId, studioId))
            .thenReturn(Optional.of(memberProfile));
        when(ratingRepository.save(any(Rating.class)))
            .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ratingService.createReview(studioId, memberId, validRequestDTO));

        assertEquals("DB error", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(memberRepository).findByMemberIdAndUser_Studio_StudioId(memberId, studioId);
        verify(ratingRepository).save(any(Rating.class));
        verifyNoInteractions(memberReviewMapper);
    }

    @Test
    void createReview_WhenRequestDTONull_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> ratingService.createReview(studioId, memberId, null));

        verifyNoInteractions(validationService, memberRepository, ratingRepository, memberReviewMapper);
    }

    // --- getReviewById tests ---

    @Test
    void getReviewById_WhenValid_ReturnsDTO() {
        doNothing().when(validationService).isStudioValid(studioId);
        doNothing().when(validationService).isMemberValid(studioId, memberId);
        when(ratingRepository.findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId))
            .thenReturn(Optional.of(rating));
        when(memberReviewMapper.toMemberReviewDTO(rating)).thenReturn(responseDTO);

        MemberReviewResponseDTO result = ratingService.getReviewById(studioId, memberId, reviewId);

        assertEquals(responseDTO, result);
        verify(validationService).isStudioValid(studioId);
        verify(validationService).isMemberValid(studioId, memberId);
        verify(ratingRepository).findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId);
        verify(memberReviewMapper).toMemberReviewDTO(rating);
    }

    @Test
    void getReviewById_WhenStudioInvalid_ThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Studio invalid")).when(validationService).isStudioValid(studioId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> ratingService.getReviewById(studioId, memberId, reviewId));

        assertEquals("Studio invalid", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verifyNoInteractions(ratingRepository, memberReviewMapper); // only these two should have no interactions
    }

    @Test
    void getReviewById_WhenMemberInvalid_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(studioId);
        doThrow(new ResourceNotFoundException("Member invalid")).when(validationService).isMemberValid(studioId, memberId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> ratingService.getReviewById(studioId, memberId, reviewId));

        assertEquals("Member invalid", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(validationService).isMemberValid(studioId, memberId);
        verifyNoInteractions(ratingRepository, memberReviewMapper);
    }

    @Test
    void getReviewById_WhenReviewNotFound_ThrowsResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(studioId);
        doNothing().when(validationService).isMemberValid(studioId, memberId);
        when(ratingRepository.findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId))
            .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
            () -> ratingService.getReviewById(studioId, memberId, reviewId));

        assertEquals("Review can't be found with id: " + reviewId, ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(validationService).isMemberValid(studioId, memberId);
        verify(ratingRepository).findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId);
        verifyNoInteractions(memberReviewMapper);
    }

    @Test
    void getReviewById_WhenRatingRepositoryThrowsException_PropagatesException() {
        doNothing().when(validationService).isStudioValid(studioId);
        doNothing().when(validationService).isMemberValid(studioId, memberId);
        when(ratingRepository.findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId))
            .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ratingService.getReviewById(studioId, memberId, reviewId));

        assertEquals("DB error", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(validationService).isMemberValid(studioId, memberId);
        verify(ratingRepository).findByRatingIdAndMemberProfile_MemberIdAndMemberProfile_User_Studio_StudioId(reviewId, memberId, studioId);
        verifyNoInteractions(memberReviewMapper);
    }
}
