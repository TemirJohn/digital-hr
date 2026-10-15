package kz.janapost.digitalhr.auth.dto;

import jakarta.validation.constraints.*;
import kz.janapost.digitalhr.common.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    private Long departmentId;

    private Long employeeId;
}
