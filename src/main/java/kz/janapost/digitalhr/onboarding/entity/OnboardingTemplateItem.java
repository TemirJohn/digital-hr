package kz.janapost.digitalhr.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "onboarding_template_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTemplateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}
