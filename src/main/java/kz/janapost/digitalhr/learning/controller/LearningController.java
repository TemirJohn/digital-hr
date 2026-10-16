package kz.janapost.digitalhr.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.janapost.digitalhr.common.dto.ApiResponse;
import kz.janapost.digitalhr.learning.dto.CourseAssignRequest;
import kz.janapost.digitalhr.learning.dto.CourseAssignmentDto;
import kz.janapost.digitalhr.learning.dto.CourseDto;
import kz.janapost.digitalhr.learning.dto.CourseProgressUpdateRequest;
import kz.janapost.digitalhr.learning.service.LearningService;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
@Tag(name = "Jana Academy (LMS)", description = "Corporate LMS: courses, assignments, progress tracking")
public class LearningController {

    private final LearningService learningService;

    @GetMapping("/courses")
    @Operation(summary = "Get corporate course catalog")
    public ApiResponse<List<CourseDto>> getAllCourses() {
        return ApiResponse.ok(learningService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Get course details by ID")
    public ApiResponse<CourseDto> getCourseById(@PathVariable Long id) {
        return ApiResponse.ok(learningService.getCourseById(id));
    }

    @GetMapping("/my-courses")
    @Operation(summary = "Get currently assigned courses for authenticated employee with progress")
    public ApiResponse<List<CourseAssignmentDto>> getMyCourses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(learningService.getMyAssignments(userDetails.getEmployeeId()));
    }

    @PatchMapping("/assignments/{id}/progress")
    @Operation(summary = "Update learning progress (0-100%). At 100% course is marked as completed.")
    public ApiResponse<CourseAssignmentDto> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody CourseProgressUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(
                learningService.updateProgress(id, request, userDetails.getEmployeeId(), userDetails.getId()),
                "Progress updated successfully"
        );
    }

    @PostMapping("/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR', 'DIRECTOR')")
    @Operation(summary = "Assign course to employee (SUPER_ADMIN, HR, DIRECTOR)")
    public ApiResponse<CourseAssignmentDto> assignCourse(
            @Valid @RequestBody CourseAssignRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(
                learningService.assignCourse(request, userDetails.getId()),
                "Course assigned successfully"
        );
    }
}
