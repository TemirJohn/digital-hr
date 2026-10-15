package kz.janapost.digitalhr.employee.repository;

import kz.janapost.digitalhr.employee.entity.EmployeeAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeAuditLogRepository extends JpaRepository<EmployeeAuditLog, Long> {
    List<EmployeeAuditLog> findByEmployeeIdOrderByChangedAtDesc(Long employeeId);
}
