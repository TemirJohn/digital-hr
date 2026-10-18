package kz.janapost.digitalhr.department.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.department.dto.DepartmentCreateRequest;
import kz.janapost.digitalhr.department.dto.DepartmentDto;
import kz.janapost.digitalhr.department.dto.DepartmentTreeDto;
import kz.janapost.digitalhr.department.dto.DepartmentUpdateRequest;
import kz.janapost.digitalhr.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Organization structure management")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all active departments (flat list)")
    public ApiResponse<List<DepartmentDto>> getAll() {
        return ApiResponse.ok(departmentService.getAll());
    }

    @GetMapping("/tree")
    @Operation(summary = "Get department hierarchy tree")
    public ApiResponse<List<DepartmentTreeDto>> getTree() {
        return ApiResponse.ok(departmentService.getTree());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ApiResponse<DepartmentDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(departmentService.getById(id));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get direct child departments")
    public ApiResponse<List<DepartmentDto>> getChildren(@PathVariable Long id) {
        return ApiResponse.ok(departmentService.getChildren(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR')")
    @Operation(summary = "Create department (SUPER_ADMIN, HR, DIRECTOR)")
    public ApiResponse<DepartmentDto> create(@Valid @RequestBody DepartmentCreateRequest request) {
        return ApiResponse.ok(departmentService.create(request), "Department created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR')")
    @Operation(summary = "Update department (SUPER_ADMIN, HR, DIRECTOR)")
    public ApiResponse<DepartmentDto> update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        return ApiResponse.ok(departmentService.update(id, request), "Department updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Soft delete department (SUPER_ADMIN only)")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ApiResponse.ok(null, "Department deactivated successfully");
    }
}
