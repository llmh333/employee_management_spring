package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreatePositionRequestDto {
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String title;
    private String description;
    private BigDecimal baseSalary;
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private Long departmentId;
}
