package com.hit.employee_management_spring.domain.mapper;

import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import com.hit.employee_management_spring.domain.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "positionId", source = "position.id")
    @Mapping(target = "positionTitle", source = "position.title")
    @Mapping(target = "departmentId", source = "position.department.id")
    @Mapping(target = "departmentName", source = "position.department.name")
    EmployeeResponseDto toDto(Employee employee);
}
