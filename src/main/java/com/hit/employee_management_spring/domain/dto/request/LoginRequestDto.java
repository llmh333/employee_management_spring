package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "Login request payload")
public class LoginRequestDto {

    @Schema(description = "Username or email address", example = "john.doe or john@example.com")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String usernameOrEmail;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String password;
}
