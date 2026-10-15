package kz.janapost.digitalhr.onboarding.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTemplateDto {
    private Long id;
    private String name;
    private Long departmentId;
    private Long createdBy;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<OnboardingTemplateItemDto> items = new ArrayList<>();
}
