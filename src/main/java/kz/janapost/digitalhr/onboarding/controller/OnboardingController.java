package kz.janapost.digitalhr.onboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.onboarding.dto.*;
import kz.janapost.digitalhr.onboarding.service.OnboardingService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Employee onboarding templates and checklists")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Get all onboarding templates")
    public ApiResponse<List<OnboardingTemplateDto>> getTemplates() {
        return ApiResponse.ok(onboardingService.getTemplates());
    }

    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Create onboarding template")
    public ApiResponse<OnboardingTemplateDto> createTemplate(
            @Valid @RequestBody OnboardingTemplateCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ApiResponse.ok(onboardingService.createTemplate(request, userId), "Template created successfully");
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Update onboarding template")
    public ApiResponse<OnboardingTemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody OnboardingTemplateCreateRequest request
    ) {
        return ApiResponse.ok(onboardingService.updateTemplate(id, request), "Template updated successfully");
    }

    @GetMapping("/employees/{employeeId}")
    @Operation(summary = "Get employee's active onboarding checklist")
    public ApiResponse<OnboardingChecklistDto> getEmployeeChecklist(@PathVariable Long employeeId) {
        return ApiResponse.ok(onboardingService.getEmployeeChecklist(employeeId));
    }

    @PostMapping("/checklists")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Assign onboarding checklist to employee")
    public ApiResponse<OnboardingChecklistDto> createChecklist(@Valid @RequestBody ChecklistCreateRequest request) {
        return ApiResponse.ok(onboardingService.createChecklist(request), "Checklist assigned successfully");
    }

    @PatchMapping("/tasks/{taskId}/complete")
    @Operation(summary = "Mark onboarding task as completed")
    public ApiResponse<OnboardingTaskDto> completeTask(@PathVariable Long taskId) {
        return ApiResponse.ok(onboardingService.completeTask(taskId), "Task completed successfully");
    }
}
