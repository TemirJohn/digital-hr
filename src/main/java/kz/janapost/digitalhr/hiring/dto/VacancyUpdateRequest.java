package kz.janapost.digitalhr.hiring.dto;

import kz.janapost.digitalhr.common.enums.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyUpdateRequest {
    private String title;
    private String description;
    private Long departmentId;
    private String city;
    private VacancyStatus status;
    private String requirements;
    private String salaryRange;
}
