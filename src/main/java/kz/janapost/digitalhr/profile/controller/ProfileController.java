package kz.janapost.digitalhr.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.profile.dto.EmployeeProfileDto;
import kz.janapost.digitalhr.profile.service.ProfileService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Employee Profile (Personal Cabinet)", description = "Employee personal dashboard profile with company info, vacation balance, manager, LMS courses and notifications")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get personal cabinet profile for currently authenticated employee")
    public ApiResponse<EmployeeProfileDto> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(profileService.getProfile(userDetails));
    }
}
