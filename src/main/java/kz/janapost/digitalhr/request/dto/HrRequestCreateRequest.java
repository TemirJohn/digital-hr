package kz.janapost.digitalhr.request.dto;

import jakarta.validation.constraints.NotNull;
import kz.janapost.digitalhr.common.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrRequestCreateRequest {

    @NotNull(message = "Request type is required (VACATION, ADVANCE, SICK_LEAVE, OTHER)")
    private RequestType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal amount;

    private String comment;
}
