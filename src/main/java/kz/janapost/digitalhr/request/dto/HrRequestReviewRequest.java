package kz.janapost.digitalhr.request.dto;

import jakarta.validation.constraints.NotNull;
import kz.janapost.digitalhr.common.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrRequestReviewRequest {

    @NotNull(message = "Review status is required (APPROVED or REJECTED)")
    private RequestStatus status;

    private String rejectionReason;
}
