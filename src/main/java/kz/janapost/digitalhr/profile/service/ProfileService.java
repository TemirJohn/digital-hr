package kz.janapost.digitalhr.profile.service;

import kz.janapost.digitalhr.common.enums.CourseStatus;
import kz.janapost.digitalhr.common.enums.RequestStatus;
import kz.janapost.digitalhr.common.enums.RequestType;
import kz.janapost.digitalhr.common.enums.Role;
import kz.janapost.digitalhr.department.entity.Department;
import kz.janapost.digitalhr.department.repository.DepartmentRepository;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import kz.janapost.digitalhr.learning.entity.CourseAssignment;
import kz.janapost.digitalhr.learning.repository.CourseAssignmentRepository;
import kz.janapost.digitalhr.notification.repository.NotificationRepository;
import kz.janapost.digitalhr.profile.dto.EmployeeProfileDto;
import kz.janapost.digitalhr.request.entity.HrRequest;
import kz.janapost.digitalhr.request.repository.HrRequestRepository;
import kz.janapost.digitalhr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final HrRequestRepository hrRequestRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final NotificationRepository notificationRepository;

    public EmployeeProfileDto getProfile(CustomUserDetails userDetails) {
        Long employeeId = userDetails.getEmployeeId();
        Optional<Employee> employeeOpt = employeeId != null ? employeeRepository.findById(employeeId) : Optional.empty();

        Long deptId = userDetails.getDepartmentId();
        if (deptId == null && employeeOpt.isPresent()) {
            deptId = employeeOpt.get().getDepartmentId();
        }

        Optional<Department> deptOpt = deptId != null ? departmentRepository.findById(deptId) : Optional.empty();
        String deptName = deptOpt.map(Department::getName).orElse("Головной офис JanaPost");
        String city = employeeOpt.map(Employee::getCity).orElse(deptOpt.map(Department::getCity).orElse("Алматы"));

        String firstName = employeeOpt.map(Employee::getFirstName).orElse("Пользователь");
        String lastName = employeeOpt.map(Employee::getLastName).orElse("");
        String middleName = employeeOpt.map(Employee::getMiddleName).orElse("");
        String fullName = (lastName + " " + firstName + " " + (middleName != null ? middleName : "")).trim();
        String position = employeeOpt.map(Employee::getPosition).orElse("Специалист");
        LocalDate hireDate = employeeOpt.map(Employee::getHireDate).orElse(LocalDate.now());

        // Расчёт отпускных дней (по регламенту JanaPost: 24 дня, при стаже > 5 лет — 28 дней)
        int tenureYears = (int) ChronoUnit.YEARS.between(hireDate, LocalDate.now());
        int vacationDaysTotal = tenureYears >= 5 ? 28 : 24;

        int currentYear = LocalDate.now().getYear();
        int vacationDaysUsed = 0;
        if (employeeId != null) {
            List<HrRequest> myRequests = hrRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
            for (HrRequest r : myRequests) {
                if (r.getType() == RequestType.VACATION && r.getStatus() == RequestStatus.APPROVED
                        && r.getStartDate() != null && r.getEndDate() != null
                        && r.getStartDate().getYear() == currentYear) {
                    vacationDaysUsed += (int) ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1;
                }
            }
        }
        int vacationDaysLeft = Math.max(0, vacationDaysTotal - vacationDaysUsed);

        // Курсы в Jana Academy
        long activeCourses = 0;
        long completedCourses = 0;
        if (employeeId != null) {
            List<CourseAssignment> assignments = courseAssignmentRepository.findByEmployeeIdOrderByAssignedAtDesc(employeeId);
            activeCourses = assignments.stream().filter(a -> a.getStatus() != CourseStatus.COMPLETED).count();
            completedCourses = assignments.stream().filter(a -> a.getStatus() == CourseStatus.COMPLETED).count();
        }

        // Непрочитанные уведомления
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userDetails.getId());

        // Руководитель
        String managerName = findManagerName(deptOpt.orElse(null));

        Role roleEnum = null;
        try {
            roleEnum = Role.valueOf(userDetails.getRole());
        } catch (Exception ignored) {}

        return EmployeeProfileDto.builder()
                .userId(userDetails.getId())
                .employeeId(employeeId)
                .email(userDetails.getEmail())
                .role(roleEnum)
                .fullName(fullName)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .company("JanaPost")
                .position(position)
                .departmentId(deptId)
                .departmentName(deptName)
                .city(city)
                .managerName(managerName)
                .hireDate(hireDate)
                .vacationDaysTotal(vacationDaysTotal)
                .vacationDaysUsed(vacationDaysUsed)
                .vacationDaysLeft(vacationDaysLeft)
                .activeCoursesCount(activeCourses)
                .completedCoursesCount(completedCourses)
                .unreadNotificationsCount(unreadCount)
                .build();
    }

    private String findManagerName(Department department) {
        if (department == null) {
            return "Сулейменов Арман (CEO)";
        }
        if (department.getParent() != null && department.getParent().getName().contains("Филиал")) {
            return "Сатпаев Тимур (Менеджер по городу)";
        }
        return "Касымова Динара (COO)";
    }
}
