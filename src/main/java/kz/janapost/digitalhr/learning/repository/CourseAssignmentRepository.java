package kz.janapost.digitalhr.learning.repository;

import kz.janapost.digitalhr.learning.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
    List<CourseAssignment> findByEmployeeIdOrderByAssignedAtDesc(Long employeeId);
    boolean existsByCourseIdAndEmployeeId(Long courseId, Long employeeId);
}
