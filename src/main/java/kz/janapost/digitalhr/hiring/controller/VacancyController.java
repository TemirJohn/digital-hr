package kz.janapost.digitalhr.hiring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.common.dto.PageResponse;
import kz.janapost.digitalhr.hiring.dto.VacancyCreateRequest;
import kz.janapost.digitalhr.hiring.dto.VacancyDto;
import kz.janapost.digitalhr.hiring.dto.VacancyUpdateRequest;
import kz.janapost.digitalhr.hiring.service.VacancyService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
@Tag(name = "Vacancies", description = "Job vacancy management")
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    @Operation(summary = "Get vacancies with filtering and pagination")
    public ApiResponse<PageResponse<VacancyDto>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(vacancyService.getAll(status, departmentId, city, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vacancy by ID")
    public ApiResponse<VacancyDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(vacancyService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER', 'FRANCHISE_OWNER')")
    @Operation(summary = "Create vacancy")
    public ApiResponse<VacancyDto> create(
            @Valid @RequestBody VacancyCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.ok(vacancyService.create(request, userId), "Vacancy created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER', 'FRANCHISE_OWNER')")
    @Operation(summary = "Update vacancy")
    public ApiResponse<VacancyDto> update(
            @PathVariable Long id,
            @Valid @RequestBody VacancyUpdateRequest request
    ) {
        return ApiResponse.ok(vacancyService.update(id, request), "Vacancy updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Close vacancy")
    public ApiResponse<Void> close(@PathVariable Long id) {
        vacancyService.close(id);
        return ApiResponse.ok(null, "Vacancy closed successfully");
    }
}
