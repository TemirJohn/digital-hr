package kz.janapost.digitalhr.onboarding.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTaskDto {
    private Long id;
    private Long checklistId;
    private Long templateItemId;
    private String title;
    private String description;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDate dueDate;
}
