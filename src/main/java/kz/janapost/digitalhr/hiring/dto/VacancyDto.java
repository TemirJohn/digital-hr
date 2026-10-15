package kz.janapost.digitalhr.hiring.dto;

import kz.janapost.digitalhr.common.enums.VacancyStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyDto {
    private Long id;
    private String title;
    private String description;
    private Long departmentId;
    private String city;
    private VacancyStatus status;
    private String requirements;
    private String salaryRange;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
