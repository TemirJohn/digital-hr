package kz.janapost.digitalhr.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String email;
    private String role;
    private Long departmentId;
    private Long employeeId;
}
