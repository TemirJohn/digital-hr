package kz.janapost.digitalhr.employee.repository;

import kz.janapost.digitalhr.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findByDepartmentId(Long departmentId);
    List<Employee> findByCity(String city);
    java.util.Optional<Employee> findByEmail(String email);
}
