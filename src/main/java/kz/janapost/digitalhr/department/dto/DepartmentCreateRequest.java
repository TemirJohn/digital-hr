package kz.janapost.digitalhr.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.janapost.digitalhr.common.enums.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    @NotNull(message = "Department type is required")
    private DepartmentType type;

    private Long parentId;

    private String city;
}
