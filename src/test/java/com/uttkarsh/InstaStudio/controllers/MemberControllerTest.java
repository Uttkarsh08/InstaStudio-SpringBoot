package com.uttkarsh.InstaStudio.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.uttkarsh.InstaStudio.dto.member.MemberRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MemberRequestDTO validRequest;
    private MemberResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new MemberRequestDTO(
                "member@example.com",
                50000L,
                "Photography",
                1L
        );

        validResponse = new MemberResponseDTO(
                10L,
                "John Doe",
                "member@example.com",
                "1234567890",
                50000L,
                "Photography",
                4L
        );
    }

    // ========== GET BY ID ==========

    @Test
    void getMemberById_success() {
        when(memberService.getMemberById(1L, 10L)).thenReturn(validResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.getMemberById(1L, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validResponse, response.getBody());
    }

    @Test
    void getMemberById_notFound() {
        when(memberService.getMemberById(1L, 10L)).thenThrow(new ResourceNotFoundException("Member not Found"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberController.getMemberById(1L, 10L);
        });
        assertEquals("Member not Found", exception.getMessage());
    }

    // ========== CREATE MEMBER ==========

    @Test
    void createMember_success() {
        when(memberService.createMember(validRequest)).thenReturn(validResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.createMember(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(validResponse, response.getBody());
    }

    @Test
    void createMember_serviceThrowsException() {
        when(memberService.createMember(any())).thenThrow(new RuntimeException("Service failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberController.createMember(validRequest);
        });

        assertEquals("Service failed", exception.getMessage());
    }

    // ========== UPDATE MEMBER ==========

    @Test
    void updateMember_success() {
        when(memberService.updateMemberById(eq(1L), eq(10L), any(MemberRequestDTO.class))).thenReturn(validResponse);

        ResponseEntity<MemberResponseDTO> response = memberController.updateMemberById(1L, 10L, validRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validResponse, response.getBody());
    }

    @Test
    void updateMember_notFound() {
        when(memberService.updateMemberById(eq(1L), eq(10L), any(MemberRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Member not Found"));

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            memberController.updateMemberById(1L, 10L, validRequest);
        });

        assertEquals("Member not Found", thrown.getMessage());
    }


    @Test
    void updateMember_serviceThrowsException() {
        when(memberService.updateMemberById(anyLong(), anyLong(), any())).thenThrow(new RuntimeException("Update failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberController.updateMemberById(1L, 10L, validRequest);
        });

        assertEquals("Update failed", exception.getMessage());
    }

//    // ========== DELETE MEMBER ==========
//
//    @Test
//    void deleteMember_success() {
//        when(memberService.deleteMemberById(1L, 10L));
//
//        ResponseEntity<Void> response = memberController.deleteMemberById(1L, 10L);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void deleteMember_notFound() {
//        when(memberService.deleteMember(10L)).thenReturn(false);
//
//        ResponseEntity<Void> response = memberController.deleteMember(10L);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void deleteMember_serviceThrowsException() {
//        when(memberService.deleteMember(anyLong())).thenThrow(new RuntimeException("Delete error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            memberController.deleteMember(10L);
//        });
//
//        assertEquals("Delete error", exception.getMessage());
//    }

    // ========== LIST ALL MEMBERS ==========

//    @Test
//    void getAllMembers_success() {
//        List<MemberResponseDTO> list = Arrays.asList(validResponse);
//        when(memberService.getAllMemebersForStudio(1L)).thenReturn(list);
//
//        ResponseEntity<List<MemberResponseDTO>> response = memberController.getAllMembers();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(list, response.getBody());
//    }
//
//    @Test
//    void getAllMembers_emptyList() {
//        when(memberService.getAllMembers()).thenReturn(Collections.emptyList());
//
//        ResponseEntity<List<MemberResponseDTO>> response = memberController.getAllMembers();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isEmpty());
//    }
//
//    @Test
//    void getAllMembers_serviceThrowsException() {
//        when(memberService.getAllMembers()).thenThrow(new RuntimeException("Fetch failed"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            memberController.getAllMembers();
//        });
//
//        assertEquals("Fetch failed", exception.getMessage());
//    }

    // ========== VALIDATION FAILURE SIMULATION ==========

//    @Test
//    void createMember_validationFailure() {
//        // Simulate service throwing validation exception (e.g. invalid email)
//        when(memberService.createMember(any())).thenThrow(new IllegalArgumentException("Invalid email"));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            memberController.createMember(validRequest);
//        });
//
//        assertEquals("Invalid email", exception.getMessage());
//    }
//
//    @Test
//    void updateMember_validationFailure() {
//        when(memberService.updateMember(anyLong(), any())).thenThrow(new IllegalArgumentException("Invalid salary"));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            memberController.updateMember(10L, validRequest);
//        });
//
//        assertEquals("Invalid salary", exception.getMessage());
//    }
//
//    // ========== EDGE CASES ==========
//
//    @Test
//    void createMember_nullRequest() {
//        assertThrows(NullPointerException.class, () -> {
//            memberController.createMember(null);
//        });
//    }
//
//    @Test
//    void updateMember_nullRequest() {
//        assertThrows(NullPointerException.class, () -> {
//            memberController.updateMember(10L, null);
//        });
//    }
//
//    @Test
//    void getMemberById_nullId() {
//        assertThrows(NullPointerException.class, () -> {
//            memberController.getMemberById(null);
//        });
//    }
//
//    @Test
//    void deleteMember_nullId() {
//        assertThrows(NullPointerException.class, () -> {
//            memberController.deleteMember(null);
//        });
//    }

}
