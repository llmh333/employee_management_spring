package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.service.IEmployeeService;
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
@Tag(name = "Employees", description = "Employee CMS — manage employee profiles linked to users and positions (write: ADMIN only)")
@SecurityRequirement(name = "BearerAuth")
public class EmployeeController {

    private final IEmployeeService employeeService;

    @Operation(summary = "Create employee", description = "Links an existing user to a position, creating an employee record. Auto-generates employee code. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Employee created",
            content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "User is already registered as employee",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "User or position not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Employee.CREATE)
    public ResponseEntity<?> create(@RequestBody @Valid CreateEmployeeRequestDto requestDto) {
        return ApiResponseUtil.success(employeeService.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update employee", description = "Updates position, hire date, status, or notes for an employee. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employee updated",
            content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Employee or position not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PutMapping(UrlConstant.Employee.UPDATE)
    public ResponseEntity<?> update(@RequestBody @Valid UpdateEmployeeRequestDto requestDto) {
        return ApiResponseUtil.success(employeeService.update(requestDto));
    }

    @Operation(summary = "Delete employee", description = "Removes an employee record. Does not delete the linked user account. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Employee deleted"),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @DeleteMapping(UrlConstant.Employee.DELETE)
    public ResponseEntity<?> delete(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id) {
        employeeService.delete(id);
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employee found",
            content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.Employee.GET_BY_ID)
    public ResponseEntity<?> getById(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id) {
        return ApiResponseUtil.success(employeeService.getById(id));
    }

    @Operation(
        summary = "Get all employees (paginated)",
        description = "Paginated employee list with optional keyword search across firstName, lastName, and employeeCode. ADMIN only."
    )
    @ApiResponse(responseCode = "200", description = "Paginated employee list")
    @GetMapping(UrlConstant.Employee.GET_ALL)
    public ResponseEntity<?> getAll(@Valid PaginationFullRequestDto requestDto) {
        return ApiResponseUtil.success(employeeService.getAll(requestDto));
    }
}
