package kz.janapost.digitalhr.analytics.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kz.janapost.digitalhr.analytics.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    @PersistenceContext
    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<HeadcountDto> getHeadcount() {
        String sql = "SELECT COALESCE(city, 'Unknown') AS city, COUNT(*) AS cnt " +
                     "FROM employees " +
                     "WHERE status = 'ACTIVE' " +
                     "GROUP BY city " +
                     "ORDER BY cnt DESC";

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<HeadcountDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            String city = (String) row[0];
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            result.add(HeadcountDto.builder().city(city).count(count).build());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<VacancyStatsDto> getVacancyStats() {
        String sql = "SELECT status, COUNT(*) AS cnt FROM vacancies GROUP BY status ORDER BY status";

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<VacancyStatsDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            String status = (String) row[0];
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            result.add(VacancyStatsDto.builder().status(status).count(count).build());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<TurnoverDto> getTurnover() {
        String sql = "SELECT " +
                     "CAST(EXTRACT(YEAR FROM hire_date) AS INTEGER) AS yr, " +
                     "CAST(EXTRACT(MONTH FROM hire_date) AS INTEGER) AS mo, " +
                     "COUNT(*) FILTER (WHERE status != 'TERMINATED') AS hired, " +
                     "COUNT(*) FILTER (WHERE status = 'TERMINATED') AS term " +
                     "FROM employees " +
                     "GROUP BY yr, mo " +
                     "ORDER BY yr DESC, mo DESC " +
                     "LIMIT 12";

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<TurnoverDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Integer yr = row[0] != null ? ((Number) row[0]).intValue() : null;
            Integer mo = row[1] != null ? ((Number) row[1]).intValue() : null;
            Long hired = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long term = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            result.add(TurnoverDto.builder().year(yr).month(mo).hired(hired).terminated(term).build());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<HiringStatsDto> getHiringStats() {
        String sql = "SELECT " +
                     "CAST(EXTRACT(YEAR FROM hire_date) AS INTEGER) AS yr, " +
                     "CAST(EXTRACT(MONTH FROM hire_date) AS INTEGER) AS mo, " +
                     "COUNT(*) AS cnt " +
                     "FROM employees " +
                     "WHERE hire_date >= (CURRENT_DATE - INTERVAL '12 months') " +
                     "GROUP BY yr, mo " +
                     "ORDER BY yr ASC, mo ASC";

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<HiringStatsDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Integer yr = row[0] != null ? ((Number) row[0]).intValue() : null;
            Integer mo = row[1] != null ? ((Number) row[1]).intValue() : null;
            Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            result.add(HiringStatsDto.builder().year(yr).month(mo).count(count).build());
        }
        return result;
    }

    public DashboardDto getDashboard() {
        Number totalEmp = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM employees").getSingleResult();
        Number activeEmp = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM employees WHERE status = 'ACTIVE'").getSingleResult();
        Number openVac = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM vacancies WHERE status = 'OPEN'").getSingleResult();
        Number candInProg = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM candidates WHERE stage IN ('NEW', 'SCREENING', 'INTERVIEW', 'OFFER')").getSingleResult();
        Number onbInProg = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM onboarding_checklists WHERE status = 'IN_PROGRESS'").getSingleResult();

        return DashboardDto.builder()
                .totalEmployees(totalEmp != null ? totalEmp.longValue() : 0L)
                .activeEmployees(activeEmp != null ? activeEmp.longValue() : 0L)
                .openVacancies(openVac != null ? openVac.longValue() : 0L)
                .candidatesInProgress(candInProg != null ? candInProg.longValue() : 0L)
                .onboardingInProgress(onbInProg != null ? onbInProg.longValue() : 0L)
                .headcountByCity(getHeadcount())
                .build();
    }
}
