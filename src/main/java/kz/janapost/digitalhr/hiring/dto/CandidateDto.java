package kz.janapost.digitalhr.hiring.dto;

import kz.janapost.digitalhr.common.enums.CandidateStage;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String resumeUrl;
    private Long vacancyId;
    private CandidateStage stage;
    private String notes;
    private Long assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
