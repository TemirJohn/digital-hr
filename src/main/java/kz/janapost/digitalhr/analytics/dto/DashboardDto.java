package kz.janapost.digitalhr.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private Long totalEmployees;
    private Long activeEmployees;
    private Long openVacancies;
    private Long candidatesInProgress;
    private Long onboardingInProgress;

    @Builder.Default
    private List<HeadcountDto> headcountByCity = new ArrayList<>();
}
