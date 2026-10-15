package kz.janapost.digitalhr.onboarding.repository;

import kz.janapost.digitalhr.onboarding.entity.OnboardingChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnboardingChecklistRepository extends JpaRepository<OnboardingChecklist, Long> {
    List<OnboardingChecklist> findByEmployeeId(Long employeeId);
    Optional<OnboardingChecklist> findFirstByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    boolean existsByEmployeeIdAndStatus(Long employeeId, String status);
}
