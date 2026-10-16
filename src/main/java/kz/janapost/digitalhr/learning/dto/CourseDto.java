package kz.janapost.digitalhr.learning.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String contentUrl;
    private Integer durationHours;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
