package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.response.PositionResponseDto;
import com.hit.employee_management_spring.service.IPositionService;
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
@Tag(name = "Positions", description = "Position management — job titles within departments (write: ADMIN only, read: all authenticated)")
@SecurityRequirement(name = "BearerAuth")
public class PositionController {

    private final IPositionService positionService;

    @Operation(summary = "Create position", description = "Creates a new job position under a department. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Position created",
            content = @Content(schema = @Schema(implementation = PositionResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "Department not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.Position.CREATE)
    public ResponseEntity<?> create(@RequestBody @Valid CreatePositionRequestDto requestDto) {
        return ApiResponseUtil.success(positionService.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update position", description = "Updates an existing position. Can change department. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Position updated",
            content = @Content(schema = @Schema(implementation = PositionResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Position or department not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PutMapping(UrlConstant.Position.UPDATE)
    public ResponseEntity<?> update(@RequestBody @Valid UpdatePositionRequestDto requestDto) {
        return ApiResponseUtil.success(positionService.update(requestDto));
    }

    @Operation(summary = "Delete position", description = "Deletes a position. Cascades to employees assigned to this position. ADMIN only.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Position deleted"),
        @ApiResponse(responseCode = "404", description = "Position not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @DeleteMapping(UrlConstant.Position.DELETE)
    public ResponseEntity<?> delete(
            @Parameter(description = "Position ID", required = true) @PathVariable Long id) {
        positionService.delete(id);
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get position by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Position found",
            content = @Content(schema = @Schema(implementation = PositionResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Position not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.Position.GET_BY_ID)
    public ResponseEntity<?> getById(
            @Parameter(description = "Position ID", required = true) @PathVariable Long id) {
        return ApiResponseUtil.success(positionService.getById(id));
    }

    @Operation(summary = "Get all positions")
    @ApiResponse(responseCode = "200", description = "List of all positions")
    @GetMapping(UrlConstant.Position.GET_ALL)
    public ResponseEntity<?> getAll() {
        return ApiResponseUtil.success(positionService.getAll());
    }

    @Operation(summary = "Get positions by department", description = "Returns all positions belonging to a specific department.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of positions for the department"),
        @ApiResponse(responseCode = "404", description = "Department not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @GetMapping(UrlConstant.Position.GET_BY_DEPARTMENT)
    public ResponseEntity<?> getByDepartment(
            @Parameter(description = "Department ID", required = true) @PathVariable Long departmentId) {
        return ApiResponseUtil.success(positionService.getByDepartment(departmentId));
    }
}
