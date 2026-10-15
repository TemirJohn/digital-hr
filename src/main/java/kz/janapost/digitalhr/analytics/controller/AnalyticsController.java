package kz.janapost.digitalhr.analytics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.janapost.digitalhr.analytics.dto.*;
import kz.janapost.digitalhr.analytics.service.AnalyticsService;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR')")
@Tag(name = "Analytics", description = "HR analytics and metric dashboards")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/headcount")
    @Operation(summary = "Get headcount distribution by city")
    public ApiResponse<List<HeadcountDto>> getHeadcount() {
        return ApiResponse.ok(analyticsService.getHeadcount());
    }

    @GetMapping("/vacancies")
    @Operation(summary = "Get vacancy counts grouped by status")
    public ApiResponse<List<VacancyStatsDto>> getVacancyStats() {
        return ApiResponse.ok(analyticsService.getVacancyStats());
    }

    @GetMapping("/turnover")
    @Operation(summary = "Get employee turnover metrics (hired vs terminated)")
    public ApiResponse<List<TurnoverDto>> getTurnover() {
        return ApiResponse.ok(analyticsService.getTurnover());
    }

    @GetMapping("/hiring")
    @Operation(summary = "Get hiring dynamics over the last 12 months")
    public ApiResponse<List<HiringStatsDto>> getHiringStats() {
        return ApiResponse.ok(analyticsService.getHiringStats());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level HR executive dashboard metrics")
    public ApiResponse<DashboardDto> getDashboard() {
        return ApiResponse.ok(analyticsService.getDashboard());
    }
}
