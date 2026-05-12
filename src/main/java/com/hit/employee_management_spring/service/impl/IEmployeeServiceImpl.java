package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.CreateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PagingMetadata;
import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import com.hit.employee_management_spring.domain.entity.Employee;
import com.hit.employee_management_spring.domain.entity.Position;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.EmployeeMapper;
import com.hit.employee_management_spring.enums.EmployeeStatus;
import com.hit.employee_management_spring.enums.SortByConstant;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.EmployeeRepository;
import com.hit.employee_management_spring.repository.PositionRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class IEmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    private String generateEmployeeCode() {
        String code;
        do {
            int year = Year.now().getValue();
            String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            code = String.format("EMP-%d-%s", year, uniquePart);
        } while (employeeRepository.existsByEmployeeCode(code));
        return code;
    }

    @Override
    @Transactional
    public EmployeeResponseDto create(CreateEmployeeRequestDto requestDto) {
        if (employeeRepository.existsByUserId(requestDto.getUserId())) {
            throw new BadRequestException(ErrorMessage.Employee.USER_ALREADY_EMPLOYEE);
        }
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID,
                        new String[] { requestDto.getUserId() }));
        Position position = positionRepository.findById(requestDto.getPositionId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Position.NOT_FOUND,
                        new String[] { requestDto.getPositionId().toString() }));

        BigDecimal actualSalary = requestDto.getActualSalary() != null ? requestDto.getActualSalary()
                : position.getBaseSalary();

        Employee employee = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .hireDate(requestDto.getHireDate())
                .status(EmployeeStatus.ACTIVE)
                .notes(requestDto.getNotes())
                .actualSalary(actualSalary)
                .user(user)
                .position(position)
                .build();

        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponseDto update(UpdateEmployeeRequestDto requestDto) {
        Employee employee = employeeRepository.findById(requestDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.NOT_FOUND,
                        new String[] { requestDto.getId().toString() }));

        if (requestDto.getPositionId() != null) {
            Position position = positionRepository.findById(requestDto.getPositionId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Position.NOT_FOUND,
                            new String[] { requestDto.getPositionId().toString() }));
            employee.setPosition(position);
        }
        if (requestDto.getHireDate() != null) {
            employee.setHireDate(requestDto.getHireDate());
        }
        if (requestDto.getStatus() != null) {
            employee.setStatus(requestDto.getStatus());
        }
        if (requestDto.getNotes() != null) {
            employee.setNotes(requestDto.getNotes());
        }
        if (requestDto.getActualSalary() != null) {
            employee.setActualSalary(requestDto.getActualSalary());
        }
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.Employee.NOT_FOUND, new String[] { id.toString() }));
        employee.setStatus(EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.Employee.NOT_FOUND, new String[] { id.toString() }));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<?> getAll(PaginationFullRequestDto requestDto) {
        Sort sort = requestDto.getIsAscending()
                ? Sort.by(requestDto.getSortBy(SortByConstant.EMPLOYEE)).ascending()
                : Sort.by(requestDto.getSortBy(SortByConstant.EMPLOYEE)).descending();
        Pageable pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), sort);
        Page<Employee> page = employeeRepository.searchEmployees(requestDto.getKeyWords(), pageable);
        List<EmployeeResponseDto> dtos = page.getContent().stream().map(employeeMapper::toDto).toList();

        PagingMetadata meta = new PagingMetadata();
        meta.setPageNum(requestDto.getPageNum());
        meta.setPageSize(requestDto.getPageSize());
        meta.setTotalPages(page.getTotalPages());
        meta.setTotalElements(page.getTotalElements());
        meta.setSortBy(requestDto.getSortBy());
        meta.setSortType(requestDto.getIsAscending() ? "ASC" : "DESC");

        return new PaginationResponseDto<>(meta, dtos);
    }
}
