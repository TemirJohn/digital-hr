package kz.janapost.digitalhr.employee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.common.dto.PageResponse;
import kz.janapost.digitalhr.employee.dto.*;
import kz.janapost.digitalhr.employee.service.EmployeeService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee management and audit")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get list of employees with filtering, pagination and sorting")
    public ApiResponse<PageResponse<EmployeeDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        return ApiResponse.ok(employeeService.getAll(search, departmentId, city, status, page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ApiResponse<EmployeeDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER')")
    @Operation(summary = "Create new employee")
    public ApiResponse<EmployeeDto> create(
            @Valid @RequestBody EmployeeCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.ok(employeeService.create(request, userId), "Employee created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER')")
    @Operation(summary = "Update employee")
    public ApiResponse<EmployeeDto> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.ok(employeeService.update(id, request, userId), "Employee updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Terminate employee (soft delete)")
    public ApiResponse<Void> terminate(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        employeeService.terminate(id, userId);
        return ApiResponse.ok(null, "Employee terminated successfully");
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get employee field change audit log")
    public ApiResponse<List<EmployeeHistoryDto>> getHistory(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.getHistory(id));
    }

    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Add document to employee")
    public ApiResponse<DocumentDto> addDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentAddRequest request
    ) {
        return ApiResponse.ok(employeeService.addDocument(id, request), "Document added successfully");
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Get list of employee documents")
    public ApiResponse<List<DocumentDto>> getDocuments(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.getDocuments(id));
    }
}
