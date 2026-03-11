package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.response.DepartmentResponseDto;
import com.hit.employee_management_spring.service.IDepartmentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management — CRUD operations (write: ADMIN only, read: all authenticated)")
@SecurityRequirement(name = "BearerAuth")
public class DepartmentController {

    private final IDepartmentService departmentService;

    @Operation(summary = "Create department", description = "Creates a new department. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Department created",
            content = @Content(schema = @Schema(implementation = DepartmentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "409", description = "Department name already exists",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Department.CREATE)
    public ResponseEntity<?> create(@RequestBody @Valid CreateDepartmentRequestDto requestDto) {
        return ApiResponseUtil.success(departmentService.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update department", description = "Updates an existing department by ID. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department updated",
            content = @Content(schema = @Schema(implementation = DepartmentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "Department not found",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "409", description = "Department name already taken",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PutMapping(UrlConstant.Department.UPDATE)
    public ResponseEntity<?> update(@RequestBody @Valid UpdateDepartmentRequestDto requestDto) {
        return ApiResponseUtil.success(departmentService.update(requestDto));
    }

    @Operation(summary = "Delete department", description = "Deletes a department and all its positions (cascade). ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Department deleted"),
        @ApiResponse(responseCode = "404", description = "Department not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @DeleteMapping(UrlConstant.Department.DELETE)
    public ResponseEntity<?> delete(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        departmentService.delete(id);
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get department by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department found",
            content = @Content(schema = @Schema(implementation = DepartmentResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Department not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.Department.GET_BY_ID)
    public ResponseEntity<?> getById(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        return ApiResponseUtil.success(departmentService.getById(id));
    }

    @Operation(summary = "Get all departments", description = "Returns the full list of departments.")
    @ApiResponse(responseCode = "200", description = "List of departments")
    @GetMapping(UrlConstant.Department.GET_ALL)
    public ResponseEntity<?> getAll() {
        return ApiResponseUtil.success(departmentService.getAll());
    }
}
