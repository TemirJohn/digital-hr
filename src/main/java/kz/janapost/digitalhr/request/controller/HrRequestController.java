package kz.janapost.digitalhr.request.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.request.dto.HrRequestCreateRequest;
import kz.janapost.digitalhr.request.dto.HrRequestDto;
import kz.janapost.digitalhr.request.dto.HrRequestReviewRequest;
import kz.janapost.digitalhr.request.service.HrRequestService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Tag(name = "HR Requests", description = "Employee service requests (Vacation, Advance, Sick leave)")
public class HrRequestController {

    private final HrRequestService hrRequestService;

    @GetMapping("/my")
    @Operation(summary = "Get current authenticated employee requests")
    public ApiResponse<List<HrRequestDto>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(hrRequestService.getMyRequests(userDetails.getEmployeeId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a new HR request (Vacation, Advance, etc.)")
    public ApiResponse<HrRequestDto> createRequest(
            @Valid @RequestBody HrRequestCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(
                hrRequestService.createRequest(request, userDetails.getEmployeeId(), userDetails.getId()),
                "Request submitted successfully"
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER', 'FRANCHISE_OWNER', 'FRANCHISE_MANAGER')")
    @Operation(summary = "Get all incoming HR requests for approval (Managers/HR)")
    public ApiResponse<List<HrRequestDto>> getAllRequests() {
        return ApiResponse.ok(hrRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get HR request by ID")
    public ApiResponse<HrRequestDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(hrRequestService.getById(id));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR', 'CITY_MANAGER', 'FRANCHISE_OWNER', 'FRANCHISE_MANAGER')")
    @Operation(summary = "Approve or reject HR request (triggers 1C and Documentolog integrations)")
    public ApiResponse<HrRequestDto> reviewRequest(
            @PathVariable Long id,
            @Valid @RequestBody HrRequestReviewRequest reviewRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(
                hrRequestService.reviewRequest(id, reviewRequest, userDetails.getId()),
                "Request reviewed successfully"
        );
    }
}
