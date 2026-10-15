package kz.janapost.digitalhr.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EmployeeCreateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String city;

    private EmployeeStatus status;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private LocalDate terminationDate;

    private String email;

    private String phone;

    private LocalDate birthDate;

    private String iin;
}
