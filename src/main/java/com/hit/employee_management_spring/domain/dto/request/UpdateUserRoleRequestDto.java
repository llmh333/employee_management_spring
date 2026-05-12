package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRoleRequestDto {
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String userId;

    @NotEmpty(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private List<String> roleNames;
}
