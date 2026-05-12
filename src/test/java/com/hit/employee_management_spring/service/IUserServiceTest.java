package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.enums.RoleConstant;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.enums.Gender;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.service.impl.IUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistCacheRepository tokenBlacklistCacheRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private IUserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-uuid")
                .username("testuser")
                .email("test@example.com")
                .password("encoded-password")
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .build();

        userResponseDto = new UserResponseDto();
        userResponseDto.setId("user-uuid");
        userResponseDto.setUsername("testuser");
        userResponseDto.setEmail("test@example.com");
    }

    @Nested
    @DisplayName("Add New User Tests")
    class AddNewUserTests {

        @Test
        @DisplayName("Should add new user successfully")
        void addNewUser_Success() {
            // Arrange
            RegisterUserRequestDto request = new RegisterUserRequestDto();
            request.setUsername("testuser");
            request.setEmail("test@example.com");
            request.setPassword("password");
            request.setConfirmPassword("password");

            Role roleUser = new Role();
            roleUser.setName(RoleConstant.ROLE_USER.name());

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(roleRepository.findByName(RoleConstant.ROLE_USER.name())).thenReturn(roleUser);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(any())).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

            // Act
            UserResponseDto result = userService.addNewUser(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateDataException when username exists")
        void addNewUser_UsernameExists_ThrowsDuplicateDataException() {
            // Arrange
            RegisterUserRequestDto request = new RegisterUserRequestDto();
            request.setUsername("testuser");

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.addNewUser(request))
                    .isInstanceOf(DuplicateDataException.class)
                    .hasMessage(ErrorMessage.User.USERNAME_ALREADY_EXIST);
        }

        @Test
        @DisplayName("Should throw DuplicateDataException when email exists")
        void addNewUser_EmailExists_ThrowsDuplicateDataException() {
            // Arrange
            RegisterUserRequestDto request = new RegisterUserRequestDto();
            request.setUsername("testuser");
            request.setEmail("test@example.com");

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.addNewUser(request))
                    .isInstanceOf(DuplicateDataException.class)
                    .hasMessage(ErrorMessage.User.EMAIL_ALREADY_EXIST);
        }

        @Test
        @DisplayName("Should throw BadRequestException when passwords do not match")
        void addNewUser_PasswordNotMatch_ThrowsBadRequestException() {
            // Arrange
            RegisterUserRequestDto request = new RegisterUserRequestDto();
            request.setUsername("testuser");
            request.setEmail("test@example.com");
            request.setPassword("password");
            request.setConfirmPassword("wrong-password");

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> userService.addNewUser(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorMessage.Validation.PASSWORD_NOT_MATCH);
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user by ID")
        void getUserById_Success() {
            // Arrange
            when(userRepository.findById("user-uuid")).thenReturn(Optional.of(user));
            when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

            // Act
            UserResponseDto result = userService.getUserById("user-uuid");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-uuid");
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found by ID")
        void getUserById_NotFound_ThrowsNotFoundException() {
            // Arrange
            when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById("non-existent"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.User.NOT_FOUND_BY_ID);
        }

        @Test
        @DisplayName("Should return all users paginated")
        void getAllUser_Success() {
            // Arrange
            PaginationFullRequestDto request = new PaginationFullRequestDto();
            request.setPageNum(0);
            request.setPageSize(10);
            request.setIsAscending(true);
            request.setSortBy("username");

            Page<User> userPage = new PageImpl<>(List.of(user));
            when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
            when(userMapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

            // Act
            PaginationResponseDto result = userService.getAllUser(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getMetadata().getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void updateUser_Success() {
            // Arrange
            UpdateUserRequestDto request = new UpdateUserRequestDto();
            request.setId("user-uuid");
            request.setFirstName("Jane");
            request.setLastName("Smith");
            request.setGender(Gender.FEMALE.name());

            when(userRepository.findById("user-uuid")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

            // Act
            UserResponseDto result = userService.updateUser(request);

            // Assert
            assertThat(result).isNotNull();
            verify(userRepository).save(user);
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(user.getGender()).isEqualTo(Gender.FEMALE);
        }

        @Test
        @DisplayName("Should throw NotFoundException when updating non-existent user")
        void updateUser_NotFound_ThrowsNotFoundException() {
            // Arrange
            UpdateUserRequestDto request = new UpdateUserRequestDto();
            request.setId("non-existent");

            when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.updateUser(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.User.NOT_FOUND_BY_ID);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void deleteByUserId_Success() {
            // Arrange
            when(userRepository.existsById("user-uuid")).thenReturn(true);

            // Act
            boolean result = userService.deleteByUserId("user-uuid");

            // Assert
            assertThat(result).isTrue();
            verify(userRepository).deleteById("user-uuid");
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting non-existent user")
        void deleteByUserId_NotFound_ThrowsNotFoundException() {
            // Arrange
            when(userRepository.existsById("non-existent")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteByUserId("non-existent"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.User.NOT_FOUND_BY_ID);
        }
    }

    @Nested
    @DisplayName("Reset Password Tests")
    class ResetPasswordTests {

        @Test
        @DisplayName("Should reset password successfully")
        void resetPassword_Success() {
            // Arrange
            String email = "test@example.com";
            String newPassword = "new-password";

            when(userRepository.findByEmail(email)).thenReturn(user);
            when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");
            when(userMapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

            // Act
            UserResponseDto result = userService.resetPassword(email, newPassword);

            // Assert
            assertThat(result).isNotNull();
            verify(userRepository).save(user);
            assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        }

        @Test
        @DisplayName("Should throw NotFoundException when resetting password for non-existent email")
        void resetPassword_NotFound_ThrowsNotFoundException() {
            // Arrange
            String email = "non-existent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> userService.resetPassword(email, "password"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.User.NOT_FOUND_BY_EMAIL);
        }
    }
}
