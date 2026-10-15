package kz.janapost.digitalhr.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private Long userId;
    private String email;
    private Long departmentId;
}
