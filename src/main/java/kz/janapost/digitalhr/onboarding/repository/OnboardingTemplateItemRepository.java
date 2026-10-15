package kz.janapost.digitalhr.onboarding.repository;

import kz.janapost.digitalhr.onboarding.entity.OnboardingTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnboardingTemplateItemRepository extends JpaRepository<OnboardingTemplateItem, Long> {
    List<OnboardingTemplateItem> findByTemplateIdOrderByOrderIndexAsc(Long templateId);
    void deleteByTemplateId(Long templateId);
}
