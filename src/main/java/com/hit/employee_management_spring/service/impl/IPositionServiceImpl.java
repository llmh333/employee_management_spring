package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.response.PositionResponseDto;
import com.hit.employee_management_spring.domain.entity.Department;
import com.hit.employee_management_spring.domain.entity.Position;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.DepartmentRepository;
import com.hit.employee_management_spring.repository.PositionRepository;
import com.hit.employee_management_spring.service.IPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class IPositionServiceImpl implements IPositionService {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    private PositionResponseDto toDto(Position position) {
        PositionResponseDto dto = new PositionResponseDto();
        dto.setId(position.getId());
        dto.setTitle(position.getTitle());
        dto.setDescription(position.getDescription());
        dto.setBaseSalary(position.getBaseSalary());
        dto.setDepartmentId(position.getDepartment().getId());
        dto.setDepartmentName(position.getDepartment().getName());
        dto.setCreatedAt(position.getCreatedAt());
        dto.setUpdatedAt(position.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional
    public PositionResponseDto create(CreatePositionRequestDto requestDto) {
        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.NOT_FOUND,
                        new String[]{requestDto.getDepartmentId().toString()}));
        Position position = Position.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .baseSalary(requestDto.getBaseSalary())
                .department(department)
                .build();
        return toDto(positionRepository.save(position));
    }

    @Override
    @Transactional
    public PositionResponseDto update(UpdatePositionRequestDto requestDto) {
        Position position = positionRepository.findById(requestDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Position.NOT_FOUND,
                        new String[]{requestDto.getId().toString()}));
        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.NOT_FOUND,
                        new String[]{requestDto.getDepartmentId().toString()}));
        position.setTitle(requestDto.getTitle());
        position.setDescription(requestDto.getDescription());
        position.setBaseSalary(requestDto.getBaseSalary());
        position.setDepartment(department);
        return toDto(positionRepository.save(position));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new NotFoundException(ErrorMessage.Position.NOT_FOUND, new String[]{id.toString()});
        }
        positionRepository.deleteById(id);
        return true;
    }

    @Override
    public PositionResponseDto getById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Position.NOT_FOUND, new String[]{id.toString()}));
        return toDto(position);
    }

    @Override
    public List<PositionResponseDto> getByDepartment(Long departmentId) {
        return positionRepository.findAllByDepartmentId(departmentId).stream().map(this::toDto).toList();
    }

    @Override
    public List<PositionResponseDto> getAll() {
        return positionRepository.findAll().stream().map(this::toDto).toList();
    }
}
