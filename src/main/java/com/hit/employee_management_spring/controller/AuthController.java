package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and logout")
public class AuthController {

    private final IAuthService authService;
    private final MessageSource messageSource;

    @Operation(summary = "Register a new user", description = "Creates a new user account with ROLE_USER by default")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or passwords do not match",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<RestData<?>> register(@RequestBody @Valid RegisterUserRequestDto requestDto) {
        UserResponseDto responseDto = authService.register(requestDto);
        return ApiResponseUtil.success(responseDto);
    }

    @Operation(summary = "Login", description = "Authenticate with username/email and password. Returns JWT access and refresh tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful — returns access token and refresh token",
            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<RestData<?>> login(@RequestBody @Valid LoginRequestDto requestDto) {
        LoginResponseDto responseDto = authService.login(requestDto);
        return ApiResponseUtil.success(responseDto);
    }

    @Operation(summary = "Logout", description = "Invalidates the current access token by adding it to the Redis blacklist",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Logged out successfully"),
        @ApiResponse(responseCode = "401", description = "No token provided or already invalid",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<RestData<?>> logout(HttpServletRequest request) {
        boolean resultLogout = authService.logout(request);
        if (resultLogout) {
            return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
        }
        String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, request.getLocale());
        return ApiResponseUtil.error(message, HttpStatus.UNAUTHORIZED);
    }
}
