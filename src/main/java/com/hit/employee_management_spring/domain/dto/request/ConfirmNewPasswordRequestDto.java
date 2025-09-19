package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfirmNewPasswordRequestDto {

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String tokenChangePassword;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = ErrorMessage.Validation.PASSWORD)
    private String newPassword;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = ErrorMessage.Validation.PASSWORD)
    private String confirmNewPassword;
}
