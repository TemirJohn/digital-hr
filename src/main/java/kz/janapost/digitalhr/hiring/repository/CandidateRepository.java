package kz.janapost.digitalhr.hiring.repository;

import kz.janapost.digitalhr.common.enums.CandidateStage;
import kz.janapost.digitalhr.hiring.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    List<Candidate> findByVacancyId(Long vacancyId);
    List<Candidate> findByVacancyIdAndStage(Long vacancyId, CandidateStage stage);
}
