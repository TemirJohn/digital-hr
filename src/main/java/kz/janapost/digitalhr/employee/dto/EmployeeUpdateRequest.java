package kz.janapost.digitalhr.employee.dto;

import kz.janapost.digitalhr.common.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String position;
    private Long departmentId;
    private String city;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String iin;
}
