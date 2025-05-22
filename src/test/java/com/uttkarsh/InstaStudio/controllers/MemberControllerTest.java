package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(memberController, "PAGE_SIZE", 10); // Set PAGE_SIZE
    }

    private final MemberResponseDTO sampleResponse = new MemberResponseDTO(
            1L, "John Doe", "john@example.com", "9876543210", 50000L, "Photographer", 4L
    );

    private final MemberRequestDTO sampleRequest = new MemberRequestDTO(
            "john@example.com", 50000L, "Photographer", 1L
    );

    @Test
    void createMember_Success() {
        when(memberService.createMember(any(MemberRequestDTO.class))).thenReturn(sampleResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.createMember(sampleRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleResponse, response.getBody());
    }

    @Test
    void createMember_InvalidRequest_ThrowsException() {
        assertThrows(MethodArgumentNotValidException.class, () -> {
            memberController.createMember(new MemberRequestDTO());
        });
    }

    @Test
    void getMemberById_Success() {
        when(memberService.getMemberById(1L, 2L)).thenReturn(sampleResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.getMemberById(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleResponse, response.getBody());
    }

    @Test
    void getMemberById_NotFound() {
        when(memberService.getMemberById(1L, 99L)).thenThrow(new ResourceNotFoundException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> memberController.getMemberById(1L, 99L));
    }

    @Test
    void getAllMembers_Success() {
        Page<MemberResponseDTO> page = new PageImpl<>(List.of(sampleResponse));
        when(memberService.getAllMemebersForStudio(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MemberResponseDTO>> response = memberController.getAllMembers(1L, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
    }

    @Test
    void getAllMembers_EmptyPage() {
        Page<MemberResponseDTO> page = Page.empty();
        when(memberService.getAllMemebersForStudio(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MemberResponseDTO>> response = memberController.getAllMembers(1L, 0);

        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void updateMemberById_Success() {
        when(memberService.updateMemberById(eq(1L), eq(2L), any(MemberRequestDTO.class)))
                .thenReturn(sampleResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.updateMemberById(1L, 2L, sampleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleResponse, response.getBody());
    }

    @Test
    void updateMemberById_NotFound() {
        when(memberService.updateMemberById(eq(1L), eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> memberController.updateMemberById(1L, 99L, sampleRequest));
    }

    @Test
    void deleteMemberById_Success() {
        doNothing().when(memberService).deleteMemberById(1L, 2L);

        ResponseEntity<Void> response = memberController.deleteMemberById(1L, 2L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteMemberById_NotFound() {
        doThrow(new ResourceNotFoundException("Not found"))
                .when(memberService).deleteMemberById(1L, 2L);

        assertThrows(ResourceNotFoundException.class,
                () -> memberController.deleteMemberById(1L, 2L));
    }

    @Test
    void getMemberReviewsById_Success() {
        Page<MemberReviewResponseDTO> page = new PageImpl<>(List.of(
                new MemberReviewResponseDTO(1L, 5, "Great job!", "John", LocalDateTime.now())
        ));
        when(memberService.getMemberReviewsById(eq(1L), eq(2L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MemberReviewResponseDTO>> response =
                memberController.getMemberReviewsById(1L, 2L, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
    }

    @Test
    void getMemberReviewsById_Empty() {
        when(memberService.getMemberReviewsById(eq(1L), eq(2L), any(Pageable.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<MemberReviewResponseDTO>> response =
                memberController.getMemberReviewsById(1L, 2L, 0);

        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void getAvailableMembers_Success() {
        List<MemberResponseDTO> list = List.of(sampleResponse);
        when(memberService.getALlAvailableMembers(eq(1L), any(), any())).thenReturn(list);

        ResponseEntity<List<MemberResponseDTO>> response =
                memberController.getAvailableMembers(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getAvailableMembers_Empty() {
        when(memberService.getALlAvailableMembers(eq(1L), any(), any())).thenReturn(List.of());

        ResponseEntity<List<MemberResponseDTO>> response =
                memberController.getAvailableMembers(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void getAvailableMembers_InvalidDates() {
        assertThrows(Exception.class, () -> {
            memberController.getAvailableMembers(1L, null, null);
        });
    }

    @Test
    void searchAllMembers_Success() {
        Page<MemberResponseDTO> page = new PageImpl<>(List.of(sampleResponse));
        when(memberService.searchAllMembers(eq(1L), eq("john"), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<MemberResponseDTO>> response = memberController.searchAllMembers(1L, "john", 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
    }

    @Test
    void searchAllMembers_NoResults() {
        when(memberService.searchAllMembers(eq(1L), eq("unknown"), any(Pageable.class)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<MemberResponseDTO>> response = memberController.searchAllMembers(1L, "unknown", 0);

        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

}

