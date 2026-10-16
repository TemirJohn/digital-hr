package kz.janapost.digitalhr.learning.service;

import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.enums.CourseStatus;
import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import kz.janapost.digitalhr.learning.dto.CourseAssignRequest;
import kz.janapost.digitalhr.learning.dto.CourseAssignmentDto;
import kz.janapost.digitalhr.learning.dto.CourseDto;
import kz.janapost.digitalhr.learning.dto.CourseProgressUpdateRequest;
import kz.janapost.digitalhr.learning.entity.Course;
import kz.janapost.digitalhr.learning.entity.CourseAssignment;
import kz.janapost.digitalhr.learning.repository.CourseAssignmentRepository;
import kz.janapost.digitalhr.learning.repository.CourseRepository;
import kz.janapost.digitalhr.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningService {

    private final CourseRepository courseRepository;
    private final CourseAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        return courseRepository.findByIsActiveTrueOrderByTitleAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toDto(course);
    }

    @Transactional(readOnly = true)
    public List<CourseAssignmentDto> getMyAssignments(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return assignmentRepository.findByEmployeeIdOrderByAssignedAtDesc(employeeId).stream()
                .map(this::toAssignmentDto)
                .collect(Collectors.toList());
    }

    public CourseAssignmentDto updateProgress(Long assignmentId, CourseProgressUpdateRequest request, Long employeeId, Long userId) {
        CourseAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found with id: " + assignmentId));

        if (employeeId != null && !assignment.getEmployeeId().equals(employeeId)) {
            throw new BadRequestException("You can only update progress for your own courses");
        }

        int percent = request.getProgressPercent();
        assignment.setProgressPercent(percent);

        if (percent >= 100) {
            assignment.setStatus(CourseStatus.COMPLETED);
            assignment.setCompletedAt(LocalDateTime.now());

            Course course = courseRepository.findById(assignment.getCourseId()).orElse(null);
            String courseTitle = course != null ? course.getTitle() : "курс";

            if (userId != null) {
                notificationService.createNotification(
                        userId,
                        "Обучение завершено",
                        "Поздравляем! Вы успешно завершили курс '" + courseTitle + "'. Результат зафиксирован.",
                        "COURSE_COMPLETED",
                        assignment.getCourseId(),
                        "COURSE"
                );
            }
        } else if (percent > 0) {
            assignment.setStatus(CourseStatus.IN_PROGRESS);
        } else {
            assignment.setStatus(CourseStatus.ASSIGNED);
        }

        CourseAssignment saved = assignmentRepository.save(assignment);
        return toAssignmentDto(saved);
    }

    public CourseAssignmentDto assignCourse(CourseAssignRequest request, Long currentUserId) {
        if (!employeeRepository.existsById(request.getEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId());
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (assignmentRepository.existsByCourseIdAndEmployeeId(request.getCourseId(), request.getEmployeeId())) {
            throw new BadRequestException("Course already assigned to this employee");
        }

        CourseAssignment assignment = CourseAssignment.builder()
                .courseId(request.getCourseId())
                .employeeId(request.getEmployeeId())
                .status(CourseStatus.ASSIGNED)
                .progressPercent(0)
                .build();

        CourseAssignment saved = assignmentRepository.save(assignment);

        Optional<User> employeeUser = userRepository.findAll().stream()
                .filter(u -> request.getEmployeeId().equals(u.getEmployeeId()))
                .findFirst();

        employeeUser.ifPresent(u -> notificationService.createNotification(
                u.getId(),
                "Назначен новый курс",
                "Вам назначен обязательный курс в Jana Academy: '" + course.getTitle() + "'.",
                "COURSE_ASSIGNED",
                course.getId(),
                "COURSE"
        ));

        return toAssignmentDto(saved);
    }

    private CourseDto toDto(Course c) {
        return CourseDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .contentUrl(c.getContentUrl())
                .durationHours(c.getDurationHours())
                .isActive(c.getIsActive())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private CourseAssignmentDto toAssignmentDto(CourseAssignment ca) {
        Course course = courseRepository.findById(ca.getCourseId()).orElse(null);
        return CourseAssignmentDto.builder()
                .id(ca.getId())
                .courseId(ca.getCourseId())
                .courseTitle(course != null ? course.getTitle() : "Курс #" + ca.getCourseId())
                .courseDescription(course != null ? course.getDescription() : null)
                .contentUrl(course != null ? course.getContentUrl() : null)
                .durationHours(course != null ? course.getDurationHours() : null)
                .employeeId(ca.getEmployeeId())
                .status(ca.getStatus())
                .progressPercent(ca.getProgressPercent())
                .assignedAt(ca.getAssignedAt())
                .completedAt(ca.getCompletedAt())
                .build();
    }
}
