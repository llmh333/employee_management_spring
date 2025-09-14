package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterUserRequestDto {

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = ErrorMessage.Validation.USERNAME)
    private String username;

    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String password;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String confirmPassword;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Email(message = ErrorMessage.Validation.EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String firstName;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String lastName;

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String gender;
}
