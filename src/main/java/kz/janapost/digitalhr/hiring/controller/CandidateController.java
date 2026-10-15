package kz.janapost.digitalhr.hiring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.employee.dto.EmployeeDto;
import kz.janapost.digitalhr.hiring.dto.*;
import kz.janapost.digitalhr.hiring.service.CandidateService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Candidates", description = "Recruitment candidates management")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/vacancies/{vacancyId}/candidates")
    @Operation(summary = "Get all candidates for a specific vacancy")
    public ApiResponse<List<CandidateDto>> getByVacancy(@PathVariable Long vacancyId) {
        return ApiResponse.ok(candidateService.getByVacancy(vacancyId));
    }

    @GetMapping("/candidates/{id}")
    @Operation(summary = "Get candidate by ID")
    public ApiResponse<CandidateDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(candidateService.getById(id));
    }

    @PostMapping("/candidates")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'CITY_MANAGER', 'FRANCHISE_OWNER', 'FRANCHISE_MANAGER')")
    @Operation(summary = "Add candidate to vacancy")
    public ApiResponse<CandidateDto> create(@Valid @RequestBody CandidateCreateRequest request) {
        return ApiResponse.ok(candidateService.create(request), "Candidate registered successfully");
    }

    @PutMapping("/candidates/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'CITY_MANAGER', 'FRANCHISE_OWNER', 'FRANCHISE_MANAGER')")
    @Operation(summary = "Update candidate")
    public ApiResponse<CandidateDto> update(@PathVariable Long id, @Valid @RequestBody CandidateUpdateRequest request) {
        return ApiResponse.ok(candidateService.update(id, request), "Candidate updated successfully");
    }

    @PatchMapping("/candidates/{id}/stage")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'CITY_MANAGER', 'FRANCHISE_OWNER', 'FRANCHISE_MANAGER')")
    @Operation(summary = "Change candidate recruitment stage")
    public ApiResponse<CandidateDto> changeStage(@PathVariable Long id, @Valid @RequestBody StageChangeRequest request) {
        return ApiResponse.ok(candidateService.changeStage(id, request), "Candidate stage updated successfully");
    }

    @PostMapping("/candidates/{id}/hire")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Hire candidate and create employee record")
    public ApiResponse<EmployeeDto> hire(
            @PathVariable Long id,
            @Valid @RequestBody HireCandidateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.ok(candidateService.hire(id, request, userId), "Candidate hired and employee profile created");
    }
}
