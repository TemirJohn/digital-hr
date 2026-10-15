package kz.janapost.digitalhr.department.dto;

import kz.janapost.digitalhr.common.enums.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUpdateRequest {
    private String name;
    private DepartmentType type;
    private Long parentId;
    private String city;
    private Boolean isActive;
}
