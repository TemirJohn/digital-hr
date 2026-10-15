package kz.janapost.digitalhr.onboarding.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTemplateItemDto {
    private Long id;
    private Long templateId;
    private String title;
    private String description;
    private Integer orderIndex;
}
