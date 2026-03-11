package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDepartmentRequestDto {
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private Long id;
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String name;
    private String description;
    private String location;
}
