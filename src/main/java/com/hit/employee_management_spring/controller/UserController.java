package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management — CRUD operations (ADMIN only except get self)")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final IUserService userService;
    private final MessageSource messageSource;

    @Operation(summary = "Get user by ID", description = "ADMIN can get any user. Users can only get their own profile.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied — not admin and not own profile",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.User.GET_USER_BY_ID)
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User UUID", required = true) @PathVariable String userId) {
        UserResponseDto responseDto = userService.getUserById(userId);
        return ApiResponseUtil.success(responseDto);
    }

    @Operation(summary = "Get all users (paginated)", description = "Returns a paginated list of all users. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated user list",
            content = @Content(schema = @Schema(implementation = PaginationResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.User.GET_ALL_USER)
    public ResponseEntity<?> getAllUser(@Valid PaginationFullRequestDto requestDto) {
        PaginationResponseDto responseDto = userService.getAllUser(requestDto);
        return ApiResponseUtil.success(responseDto);
    }

    @Operation(summary = "Add new user (admin)", description = "Admin creates a new user account directly. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.User.ADD_NEW_USER)
    public ResponseEntity<?> addNewUser(@RequestBody @Valid RegisterUserRequestDto requestDto) {
        UserResponseDto responseDto = userService.addNewUser(requestDto);
        return ApiResponseUtil.success(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete user by ID", description = "Permanently deletes a user account. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @DeleteMapping(UrlConstant.User.DELETE_USER_BY_ID)
    public ResponseEntity<?> deleteUserById(
            @Parameter(description = "User UUID", required = true) @PathVariable String userId) {
        boolean resultDelete = userService.deleteByUserId(userId);
        if (!resultDelete) {
            String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, LocaleContextHolder.getLocale());
            return ApiResponseUtil.error(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update user profile", description = "Updates firstName, lastName, and gender of a user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PutMapping(UrlConstant.User.UPDATE_USER)
    public ResponseEntity<?> updateUser(@RequestBody @Valid UpdateUserRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.updateUser(requestDto);
        return ApiResponseUtil.success(userResponseDto, HttpStatus.OK);
    }
}
