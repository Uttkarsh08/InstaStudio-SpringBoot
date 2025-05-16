package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.services.RatingService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private final Long studioId = 1L;
    private final Long memberId = 2L;
    private final Long reviewId = 3L;

    private MemberReviewRequestDTO requestDTO;
    private MemberReviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new MemberReviewRequestDTO();
        requestDTO.setCreatedAt(LocalDateTime.now());
        requestDTO.setReviewBy("John Doe");
        requestDTO.setReview("Great work!");
        requestDTO.setRatingValue(5);

        responseDTO = new MemberReviewResponseDTO();
        responseDTO.setRatingId(reviewId);
        responseDTO.setCreatedAt(requestDTO.getCreatedAt());
        responseDTO.setReviewBy(requestDTO.getReviewBy());
        responseDTO.setReview(requestDTO.getReview());
        responseDTO.setRatingValue(requestDTO.getRatingValue());
    }

    @Test
    void createReviewForMember_WhenValid_ReturnsOk() {
        when(ratingService.createReview(studioId, memberId, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<MemberReviewResponseDTO> response = ratingController.createReviewForMember(studioId, memberId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());

        verify(ratingService).createReview(studioId, memberId, requestDTO);
    }

    @Test
    void createReviewForMember_WhenServiceThrowsException_Propagates() {
        when(ratingService.createReview(studioId, memberId, requestDTO))
                .thenThrow(new ResourceNotFoundException("Member not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> ratingController.createReviewForMember(studioId, memberId, requestDTO));

        assertEquals("Member not found", ex.getMessage());
        verify(ratingService).createReview(studioId, memberId, requestDTO);
    }

    @Test
    void createReviewForMember_WhenRequestDTOIsInvalid_ThrowsMethodArgumentNotValidException() {

        MemberReviewRequestDTO invalidDTO = new MemberReviewRequestDTO();

        when(ratingService.createReview(studioId, memberId, invalidDTO))
                .thenThrow(new ConstraintViolationException("Invalid fields", null));

        assertThrows(ConstraintViolationException.class,
                () -> ratingController.createReviewForMember(studioId, memberId, invalidDTO));

        verify(ratingService).createReview(studioId, memberId, invalidDTO);
    }

    @Test
    void getReviewById_WhenValid_ReturnsOk() {
        when(ratingService.getReviewById(studioId, memberId, reviewId)).thenReturn(responseDTO);

        ResponseEntity<MemberReviewResponseDTO> response = ratingController.getReviewById(studioId, memberId, reviewId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());

        verify(ratingService).getReviewById(studioId, memberId, reviewId);
    }

    @Test
    void getReviewById_WhenServiceThrowsException_Propagates() {
        when(ratingService.getReviewById(studioId, memberId, reviewId))
                .thenThrow(new ResourceNotFoundException("Review not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> ratingController.getReviewById(studioId, memberId, reviewId));

        assertEquals("Review not found", ex.getMessage());
        verify(ratingService).getReviewById(studioId, memberId, reviewId);
    }

    @Test
    void createReviewForMember_WhenNullRequest_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> ratingController.createReviewForMember(studioId, memberId, null));
    }
}