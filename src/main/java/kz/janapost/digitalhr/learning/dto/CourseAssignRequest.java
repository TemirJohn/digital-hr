package kz.janapost.digitalhr.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
}
