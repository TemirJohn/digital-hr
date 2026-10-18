package kz.janapost.digitalhr.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendNotificationRequest {
    private Long employeeId;
    private Long userId;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Message is required")
    private String message;
}
