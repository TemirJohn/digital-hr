package kz.janapost.digitalhr.learning.dto;

import kz.janapost.digitalhr.common.enums.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignmentDto {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String contentUrl;
    private Integer durationHours;
    private Long employeeId;
    private CourseStatus status;
    private Integer progressPercent;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
}
