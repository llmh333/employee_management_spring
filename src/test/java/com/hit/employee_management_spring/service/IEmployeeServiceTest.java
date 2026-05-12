package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import com.hit.employee_management_spring.domain.entity.Employee;
import com.hit.employee_management_spring.domain.entity.Position;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.EmployeeMapper;
import com.hit.employee_management_spring.enums.EmployeeStatus;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.EmployeeRepository;
import com.hit.employee_management_spring.repository.PositionRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.service.impl.IEmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private IEmployeeServiceImpl employeeService;

    private User user;
    private Position position;
    private Employee employee;
    private EmployeeResponseDto employeeResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-uuid")
                .username("testuser")
                .email("test@example.com")
                .build();

        position = Position.builder()
                .id(1L)
                .title("Developer")
                .baseSalary(BigDecimal.valueOf(1000))
                .build();

        employee = Employee.builder()
                .id(1L)
                .employeeCode("EMP-2024-TEST")
                .hireDate(LocalDate.now())
                .status(EmployeeStatus.ACTIVE)
                .actualSalary(BigDecimal.valueOf(1000))
                .user(user)
                .position(position)
                .build();

        employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setId(1L);
        employeeResponseDto.setEmployeeCode("EMP-2024-TEST");
        employeeResponseDto.setUserId("user-uuid");
        employeeResponseDto.setPositionId(1L);
    }

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully")
        void create_Success() {
            // Arrange
            CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
            request.setUserId("user-uuid");
            request.setPositionId(1L);
            request.setHireDate(LocalDate.now());

            when(employeeRepository.existsByUserId(request.getUserId())).thenReturn(false);
            when(employeeRepository.existsByEmployeeCode(anyString())).thenReturn(false);
            when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
            when(positionRepository.findById(request.getPositionId())).thenReturn(Optional.of(position));
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
            when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeResponseDto);

            // Act
            EmployeeResponseDto result = employeeService.create(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(employeeRepository).save(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw BadRequestException when user is already an employee")
        void create_UserAlreadyEmployee_ThrowsBadRequestException() {
            // Arrange
            CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
            request.setUserId("user-uuid");

            when(employeeRepository.existsByUserId(request.getUserId())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> employeeService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorMessage.Employee.USER_ALREADY_EMPLOYEE);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void create_UserNotFound_ThrowsNotFoundException() {
            // Arrange
            CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
            request.setUserId("non-existent-user");

            when(employeeRepository.existsByUserId(request.getUserId())).thenReturn(false);
            when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.create(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.User.NOT_FOUND_BY_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when position not found")
        void create_PositionNotFound_ThrowsNotFoundException() {
            // Arrange
            CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
            request.setUserId("user-uuid");
            request.setPositionId(99L);

            when(employeeRepository.existsByUserId(request.getUserId())).thenReturn(false);
            when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
            when(positionRepository.findById(request.getPositionId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.create(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Position.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Update Employee Tests")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should update employee successfully")
        void update_Success() {
            // Arrange
            UpdateEmployeeRequestDto request = new UpdateEmployeeRequestDto();
            request.setId(1L);
            request.setNotes("Updated notes");
            request.setStatus(EmployeeStatus.INACTIVE);

            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
            when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeResponseDto);

            // Act
            EmployeeResponseDto result = employeeService.update(request);

            // Assert
            assertThat(result).isNotNull();
            verify(employeeRepository).save(employee);
            assertThat(employee.getNotes()).isEqualTo("Updated notes");
            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found")
        void update_EmployeeNotFound_ThrowsNotFoundException() {
            // Arrange
            UpdateEmployeeRequestDto request = new UpdateEmployeeRequestDto();
            request.setId(99L);

            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.update(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Employee.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Get Employee By ID Tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return employee when found")
        void getById_Success() {
            // Arrange
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeMapper.toDto(employee)).thenReturn(employeeResponseDto);

            // Act
            EmployeeResponseDto result = employeeService.getById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when not found")
        void getById_NotFound_ThrowsNotFoundException() {
            // Arrange
            when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.getById(1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Employee.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Delete Employee Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete employee successfully (soft delete)")
        void delete_Success() {
            // Arrange
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

            // Act
            boolean result = employeeService.delete(1L);

            // Assert
            assertThat(result).isTrue();
            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.TERMINATED);
            verify(employeeRepository).save(employee);
        }

        @Test
        @DisplayName("Should throw NotFoundException when not found")
        void delete_NotFound_ThrowsNotFoundException() {
            // Arrange
            when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.delete(1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Employee.NOT_FOUND);
        }
    }
}
