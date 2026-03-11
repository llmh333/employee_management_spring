package com.hit.employee_management_spring.domain.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PositionResponseDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal baseSalary;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
