package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CreateEmployeeRequestDto {
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String userId;
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private Long positionId;
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private LocalDate hireDate;
    private String notes;
}
