package kz.janapost.digitalhr.learning.entity;

import jakarta.persistence.*;
import kz.janapost.digitalhr.common.enums.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.ASSIGNED;

    @Builder.Default
    @Column(name = "progress_percent", nullable = false)
    private Integer progressPercent = 0;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    void prePersist() { assignedAt = LocalDateTime.now(); }
}
