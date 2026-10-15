package kz.janapost.digitalhr.onboarding.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingChecklistDto {
    private Long id;
    private Long employeeId;
    private Long templateId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Builder.Default
    private List<OnboardingTaskDto> tasks = new ArrayList<>();
}
