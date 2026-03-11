package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.EmployeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequestDto {
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private Long id;
    private Long positionId;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private String notes;
}
