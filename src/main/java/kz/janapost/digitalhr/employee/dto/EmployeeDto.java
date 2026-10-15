package kz.janapost.digitalhr.employee.dto;

import kz.janapost.digitalhr.common.enums.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
