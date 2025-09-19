package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ForgotPasswordRequestDto {

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Email(message = ErrorMessage.Validation.EMAIL)
    private String email;
}
