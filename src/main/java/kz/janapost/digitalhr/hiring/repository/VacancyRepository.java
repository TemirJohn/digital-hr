package kz.janapost.digitalhr.hiring.repository;

import kz.janapost.digitalhr.common.enums.VacancyStatus;
import kz.janapost.digitalhr.hiring.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {
    List<Vacancy> findByStatus(VacancyStatus status);
    List<Vacancy> findByDepartmentId(Long departmentId);
}
