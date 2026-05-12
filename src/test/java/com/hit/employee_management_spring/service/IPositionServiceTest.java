package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.response.PositionResponseDto;
import com.hit.employee_management_spring.domain.entity.Department;
import com.hit.employee_management_spring.domain.entity.Position;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.DepartmentRepository;
import com.hit.employee_management_spring.repository.EmployeeRepository;
import com.hit.employee_management_spring.repository.PositionRepository;
import com.hit.employee_management_spring.service.impl.IPositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IPositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private IPositionServiceImpl positionService;

    private Department department;
    private Position position;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        position = Position.builder()
                .id(1L)
                .title("Developer")
                .description("Coding")
                .baseSalary(BigDecimal.valueOf(1000.0))
                .department(department)
                .build();
    }

    @Nested
    @DisplayName("Create Position Tests")
    class CreatePositionTests {

        @Test
        @DisplayName("Should create position successfully")
        void create_Success() {
            // Arrange
            CreatePositionRequestDto request = new CreatePositionRequestDto();
            request.setTitle("Developer");
            request.setDescription("Coding");
            request.setBaseSalary(BigDecimal.valueOf(1000.0));
            request.setDepartmentId(1L);

            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(positionRepository.save(any(Position.class))).thenReturn(position);

            // Act
            PositionResponseDto result = positionService.create(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Developer");
            verify(positionRepository).save(any(Position.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when department not found")
        void create_DepartmentNotFound_ThrowsNotFoundException() {
            // Arrange
            CreatePositionRequestDto request = new CreatePositionRequestDto();
            request.setDepartmentId(99L);

            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> positionService.create(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Department.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Update Position Tests")
    class UpdatePositionTests {

        @Test
        @DisplayName("Should update position successfully")
        void update_Success() {
            // Arrange
            UpdatePositionRequestDto request = new UpdatePositionRequestDto();
            request.setId(1L);
            request.setTitle("Senior Developer");
            request.setDepartmentId(1L);

            when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(positionRepository.save(any(Position.class))).thenReturn(position);

            // Act
            PositionResponseDto result = positionService.update(request);

            // Assert
            assertThat(result).isNotNull();
            verify(positionRepository).save(position);
            assertThat(position.getTitle()).isEqualTo("Senior Developer");
        }

        @Test
        @DisplayName("Should throw NotFoundException when position not found")
        void update_PositionNotFound_ThrowsNotFoundException() {
            // Arrange
            UpdatePositionRequestDto request = new UpdatePositionRequestDto();
            request.setId(99L);

            when(positionRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> positionService.update(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Position.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Get Position Tests")
    class GetPositionTests {

        @Test
        @DisplayName("Should return position by ID")
        void getById_Success() {
            // Arrange
            when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

            // Act
            PositionResponseDto result = positionService.getById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return all positions")
        void getAll_Success() {
            // Arrange
            when(positionRepository.findAll()).thenReturn(List.of(position));

            // Act
            List<PositionResponseDto> result = positionService.getAll();

            // Assert
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should return positions by department")
        void getByDepartment_Success() {
            // Arrange
            when(positionRepository.findAllByDepartmentId(1L)).thenReturn(List.of(position));

            // Act
            List<PositionResponseDto> result = positionService.getByDepartment(1L);

            // Assert
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Delete Position Tests")
    class DeletePositionTests {

        @Test
        @DisplayName("Should delete position successfully")
        void delete_Success() {
            // Arrange
            when(positionRepository.existsById(1L)).thenReturn(true);
            when(employeeRepository.existsByPositionId(1L)).thenReturn(false);

            // Act
            boolean result = positionService.delete(1L);

            // Assert
            assertThat(result).isTrue();
            verify(positionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw BadRequestException when employees exist")
        void delete_EmployeesExist_ThrowsBadRequestException() {
            // Arrange
            when(positionRepository.existsById(1L)).thenReturn(true);
            when(employeeRepository.existsByPositionId(1L)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> positionService.delete(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorMessage.Position.DELETE_CONSTRAINT);
            verify(positionRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting non-existent position")
        void delete_NotFound_ThrowsNotFoundException() {
            // Arrange
            when(positionRepository.existsById(99L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> positionService.delete(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorMessage.Position.NOT_FOUND);
        }
    }
}
