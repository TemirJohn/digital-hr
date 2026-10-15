package kz.janapost.digitalhr.department.dto;

import kz.janapost.digitalhr.common.enums.DepartmentType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentTreeDto {
    private Long id;
    private String name;
    private DepartmentType type;
    private Long parentId;
    private String city;
    private Integer level;
    private Boolean isActive;

    @Builder.Default
    private List<DepartmentTreeDto> children = new ArrayList<>();
}
