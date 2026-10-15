package kz.janapost.digitalhr.employee.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
    private Long id;
    private Long employeeId;
    private String documentType;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}
