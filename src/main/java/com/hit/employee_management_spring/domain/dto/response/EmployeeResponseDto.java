package com.hit.employee_management_spring.domain.dto.response;

import com.hit.employee_management_spring.constant.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeResponseDto {
    private Long id;
    private String employeeCode;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private String notes;
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Long positionId;
    private String positionTitle;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
