package kz.janapost.digitalhr.hiring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String resumeUrl;
    private String notes;
    private Long assignedTo;
}
