package kz.janapost.digitalhr.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.auth.dto.*;
import kz.janapost.digitalhr.auth.service.AuthService;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication & user registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @Operation(summary = "Register a new user (SUPER_ADMIN, HR only)")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok(null, "User registered successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user info")
    public ApiResponse<UserInfoResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.ok(authService.getCurrentUser(userDetails));
    }
}
