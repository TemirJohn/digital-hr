package kz.janapost.digitalhr.profile.dto;

import kz.janapost.digitalhr.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfileDto {
    private Long userId;
    private Long employeeId;
    private String email;
    private Role role;

    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;

    private String company;
    private String position;
    private Long departmentId;
    private String departmentName;
    private String city;
    private String managerName;

    private LocalDate hireDate;
    private Integer vacationDaysTotal;
    private Integer vacationDaysUsed;
    private Integer vacationDaysLeft;

    private Long activeCoursesCount;
    private Long completedCoursesCount;
    private Long unreadNotificationsCount;
}
