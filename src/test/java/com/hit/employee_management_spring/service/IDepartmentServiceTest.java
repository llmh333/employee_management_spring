package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.response.DepartmentResponseDto;
import com.hit.employee_management_spring.domain.entity.Department;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.DepartmentRepository;
import com.hit.employee_management_spring.repository.PositionRepository;
import com.hit.employee_management_spring.service.impl.IDepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IDepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private IDepartmentServiceImpl departmentService;

    private Department department;
    private CreateDepartmentRequestDto createRequest;
    private UpdateDepartmentRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("IT")
                .description("Information Technology")
                .location("Floor 1")
                .build();

        createRequest = new CreateDepartmentRequestDto();
        createRequest.setName("IT");
        createRequest.setDescription("Information Technology");
        createRequest.setLocation("Floor 1");

        updateRequest = new UpdateDepartmentRequestDto();
        updateRequest.setId(1L);
        updateRequest.setName("IT Updated");
        updateRequest.setDescription("Updated IT");
        updateRequest.setLocation("Floor 2");
    }

    @Test
    void create_ShouldReturnDepartmentResponseDto_WhenSuccess() {
        when(departmentRepository.existsByName(createRequest.getName())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentResponseDto result = departmentService.create(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(department.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void create_ShouldThrowDuplicateDataException_WhenNameExists() {
        when(departmentRepository.existsByName(createRequest.getName())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(createRequest))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessage(ErrorMessage.Department.NAME_ALREADY_EXIST);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_ShouldReturnDepartmentResponseDto_WhenSuccess() {
        when(departmentRepository.findById(updateRequest.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.existsByNameAndIdNot(updateRequest.getName(), updateRequest.getId())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentResponseDto result = departmentService.update(updateRequest);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenDepartmentNotFound() {
        when(departmentRepository.findById(updateRequest.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(updateRequest))
                .isInstanceOf(NotFoundException.class);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowDuplicateDataException_WhenNameExistsForOtherId() {
        when(departmentRepository.findById(updateRequest.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.existsByNameAndIdNot(updateRequest.getName(), updateRequest.getId())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.update(updateRequest))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessage(ErrorMessage.Department.NAME_ALREADY_EXIST);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void delete_ShouldReturnTrue_WhenSuccess() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.existsByDepartmentId(1L)).thenReturn(false);

        boolean result = departmentService.delete(1L);

        assertThat(result).isTrue();
        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowBadRequestException_WhenPositionsExist() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorMessage.Department.DELETE_CONSTRAINT);
        verify(departmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotFound() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(NotFoundException.class);
        verify(departmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void getById_ShouldReturnDepartmentResponseDto_WhenFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentResponseDto result = departmentService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll_ShouldReturnList() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<DepartmentResponseDto> result = departmentService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(department.getName());
    }
}
