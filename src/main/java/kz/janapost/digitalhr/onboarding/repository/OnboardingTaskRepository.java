package kz.janapost.digitalhr.onboarding.repository;

import kz.janapost.digitalhr.onboarding.entity.OnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask, Long> {
    List<OnboardingTask> findByChecklistId(Long checklistId);
    List<OnboardingTask> findByChecklistIdAndIsCompleted(Long checklistId, Boolean isCompleted);
}
