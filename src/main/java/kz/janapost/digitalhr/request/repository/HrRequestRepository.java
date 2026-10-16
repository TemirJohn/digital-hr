package kz.janapost.digitalhr.request.repository;

import kz.janapost.digitalhr.request.entity.HrRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrRequestRepository extends JpaRepository<HrRequest, Long> {
    List<HrRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<HrRequest> findAllByOrderByCreatedAtDesc();
}
