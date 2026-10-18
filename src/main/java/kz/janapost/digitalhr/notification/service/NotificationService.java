package kz.janapost.digitalhr.notification.service;

import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.notification.dto.NotificationDto;
import kz.janapost.digitalhr.notification.dto.SendNotificationRequest;
import kz.janapost.digitalhr.notification.entity.Notification;
import kz.janapost.digitalhr.notification.repository.NotificationRepository;
import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public NotificationDto markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }

        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return toDto(saved);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            if (Boolean.FALSE.equals(n.getIsRead())) {
                n.setIsRead(true);
            }
        }
        notificationRepository.saveAll(list);
    }

    public void createNotification(Long userId, String title, String message, String type, Long referenceId, String referenceType) {
        if (userId == null) return;
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();
        notificationRepository.save(notification);
    }

    public void sendToUserOrEmployee(SendNotificationRequest request) {
        Long targetUserId = request.getUserId();
        if (targetUserId == null && request.getEmployeeId() != null) {
            targetUserId = userRepository.findByEmployeeId(request.getEmployeeId())
                    .map(User::getId)
                    .orElseGet(() -> employeeRepository.findById(request.getEmployeeId())
                            .flatMap(emp -> emp.getEmail() != null ? userRepository.findByEmail(emp.getEmail()) : java.util.Optional.empty())
                            .map(User::getId)
                            .orElse(null));
        }

        if (targetUserId != null) {
            createNotification(targetUserId, request.getTitle(), request.getMessage(), "MESSAGE", request.getEmployeeId(), "EMPLOYEE");
        }
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .build();
    }
}
