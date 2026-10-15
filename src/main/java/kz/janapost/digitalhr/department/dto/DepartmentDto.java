package kz.janapost.digitalhr.department.dto;

import kz.janapost.digitalhr.common.enums.DepartmentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDto {
    private Long id;
    private String name;
    private DepartmentType type;
    private Long parentId;
    private String city;
    private Integer level;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
