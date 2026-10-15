package kz.janapost.digitalhr.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HiringStatsDto {
    private Integer year;
    private Integer month;
    private Long count;
}
