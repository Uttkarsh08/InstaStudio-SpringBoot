    package com.uttkarsh.InstaStudio.services.serviceImpl;

    import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
    import com.uttkarsh.InstaStudio.entities.User;
    import com.uttkarsh.InstaStudio.entities.enums.UserType;
    import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAddedException;
    import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
    import com.uttkarsh.InstaStudio.repositories.UserRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.modelmapper.ModelMapper;
    import org.springframework.test.util.ReflectionTestUtils;

    import java.time.LocalDateTime;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    class UserServiceImplTest {

        @InjectMocks
        private UserServiceImpl userService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private ModelMapper modelMapper;

        @Test
        void shouldReturnUserIfFirebaseIdExists() {
            User user = new User();
            user.setFirebaseId("firebase123");
            when(userRepository.getUserByFirebaseId("firebase123")).thenReturn(Optional.of(user));

            User result = userService.getUserByFirebaseId("firebase123");
            assertNotNull(result);
            assertEquals("firebase123", result.getFirebaseId());
        }

        @Test
        void shouldReturnNullIfFirebaseIdDoesNotExist() {
            when(userRepository.getUserByFirebaseId("no_such_id")).thenReturn(Optional.empty());

            User result = userService.getUserByFirebaseId("no_such_id");

            assertNull(result);
        }

        @Test
        void shouldReturnNullIfUserNotFound() {
            when(userRepository.getUserByFirebaseId("nonexistent")).thenReturn(Optional.empty());

            User result = userService.getUserByFirebaseId("nonexistent");
            assertNull(result);
        }

        @Test
        void shouldThrowExceptionIfUserAlreadyExists() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.setFirebaseId("firebase123");
            when(userRepository.existsByFirebaseId("firebase123")).thenReturn(true);

            assertThrows(EventAlreadyAddedException.class, () -> userService.createUser(dto));
        }


        @Test
        void shouldCreateUserWhenFirebaseIdDoesNotExist() {
            when(userRepository.existsByFirebaseId("unique_id")).thenReturn(false);

            UserRequestDTO requestDTO = new UserRequestDTO();
            requestDTO.setFirebaseId("unique_id");
            requestDTO.setUserName("New User");
            requestDTO.setUserEmail("newuser@example.com");
            requestDTO.setUserPhoneNo("9876543210");
            requestDTO.setRegistrationDate(LocalDateTime.now());
            requestDTO.setUserType(UserType.ADMIN);

            User savedUser = new User();
            savedUser.setFirebaseId("unique_id");
            savedUser.setUserName("New User");
            savedUser.setUserEmail("newuser@example.com");
            savedUser.setUserPhoneNo("9876543210");
            savedUser.setUserType(UserType.ADMIN);

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            User result = userService.createUser(requestDTO);

            assertNotNull(result);
            assertEquals("unique_id", result.getFirebaseId());
            assertEquals("New User", result.getUserName());
            assertEquals(UserType.ADMIN, result.getUserType());
        }

        @Test
        void shouldReturnTrueIfUserExistsByFirebaseId() {
            when(userRepository.existsByFirebaseId("exists_id")).thenReturn(true);

            assertTrue(userService.existsByFirebaseId("exists_id"));
        }

        @Test
        void shouldReturnFalseIfUserDoesNotExistByFirebaseId() {
            when(userRepository.existsByFirebaseId("not_exists_id")).thenReturn(false);

            assertFalse(userService.existsByFirebaseId("not_exists_id"));
        }
    }
