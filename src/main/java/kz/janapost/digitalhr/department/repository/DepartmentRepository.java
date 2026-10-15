package kz.janapost.digitalhr.department.repository;

import kz.janapost.digitalhr.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByParentId(Long parentId);
    List<Department> findByParentIsNull();
    List<Department> findAllByIsActiveTrue();
    boolean existsByNameAndParentId(String name, Long parentId);
}
