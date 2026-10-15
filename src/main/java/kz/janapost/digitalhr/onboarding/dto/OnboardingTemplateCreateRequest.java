package kz.janapost.digitalhr.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingTemplateCreateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    private Long departmentId;

    @Builder.Default
    @Valid
    private List<ItemRequest> items = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest {
        @NotBlank(message = "Item title is required")
        private String title;

        private String description;

        private Integer orderIndex;
    }
}
