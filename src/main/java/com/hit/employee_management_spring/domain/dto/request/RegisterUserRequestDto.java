package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Register new user request payload")
public class RegisterUserRequestDto {

    @Schema(description = "Username (alphanumeric, min 6 chars)", example = "johndoe")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = ErrorMessage.Validation.USERNAME)
    private String username;

    @Schema(description = "Password (alphanumeric, min 6 chars)", example = "pass123")
    @NotNull(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = ErrorMessage.Validation.PASSWORD)
    private String password;

    @Schema(description = "Must match password", example = "pass123")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String confirmPassword;

    @Schema(description = "Valid email address", example = "john@example.com")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Email(message = ErrorMessage.Validation.EMAIL)
    private String email;

    @Schema(description = "First name", example = "John")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String lastName;

    @Schema(description = "Gender (MALE / FEMALE / OTHER)", example = "MALE")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String gender;
}
