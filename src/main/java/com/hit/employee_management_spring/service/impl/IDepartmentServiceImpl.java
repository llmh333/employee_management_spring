package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.response.DepartmentResponseDto;
import com.hit.employee_management_spring.domain.entity.Department;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.DepartmentRepository;
import com.hit.employee_management_spring.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class IDepartmentServiceImpl implements IDepartmentService {

    private final DepartmentRepository departmentRepository;

    private DepartmentResponseDto toDto(Department dept) {
        DepartmentResponseDto dto = new DepartmentResponseDto();
        dto.setId(dept.getId());
        dto.setName(dept.getName());
        dto.setDescription(dept.getDescription());
        dto.setLocation(dept.getLocation());
        dto.setCreatedAt(dept.getCreatedAt());
        dto.setUpdatedAt(dept.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional
    public DepartmentResponseDto create(CreateDepartmentRequestDto requestDto) {
        if (departmentRepository.existsByName(requestDto.getName())) {
            throw new DuplicateDataException(ErrorMessage.Department.NAME_ALREADY_EXIST);
        }
        Department department = Department.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .location(requestDto.getLocation())
                .build();
        return toDto(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentResponseDto update(UpdateDepartmentRequestDto requestDto) {
        Department department = departmentRepository.findById(requestDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.NOT_FOUND, new String[]{requestDto.getId().toString()}));
        if (departmentRepository.existsByNameAndIdNot(requestDto.getName(), requestDto.getId())) {
            throw new DuplicateDataException(ErrorMessage.Department.NAME_ALREADY_EXIST);
        }
        department.setName(requestDto.getName());
        department.setDescription(requestDto.getDescription());
        department.setLocation(requestDto.getLocation());
        return toDto(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new NotFoundException(ErrorMessage.Department.NOT_FOUND, new String[]{id.toString()});
        }
        departmentRepository.deleteById(id);
        return true;
    }

    @Override
    public DepartmentResponseDto getById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.NOT_FOUND, new String[]{id.toString()}));
        return toDto(department);
    }

    @Override
    public List<DepartmentResponseDto> getAll() {
        return departmentRepository.findAll().stream().map(this::toDto).toList();
    }
}
