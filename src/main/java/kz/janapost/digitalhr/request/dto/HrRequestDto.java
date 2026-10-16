package kz.janapost.digitalhr.request.dto;

import kz.janapost.digitalhr.common.enums.RequestStatus;
import kz.janapost.digitalhr.common.enums.RequestType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrRequestDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private RequestType type;
    private RequestStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private BigDecimal amount;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long reviewedBy;
    private String reviewerName;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
}
