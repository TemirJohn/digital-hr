package kz.janapost.digitalhr.request.service;

import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.enums.RequestStatus;
import kz.janapost.digitalhr.common.enums.RequestType;
import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import kz.janapost.digitalhr.integration.documentolog.DocumentologIntegrationService;
import kz.janapost.digitalhr.integration.onec.OneCIntegrationService;
import kz.janapost.digitalhr.notification.service.NotificationService;
import kz.janapost.digitalhr.request.dto.HrRequestCreateRequest;
import kz.janapost.digitalhr.request.dto.HrRequestDto;
import kz.janapost.digitalhr.request.dto.HrRequestReviewRequest;
import kz.janapost.digitalhr.request.entity.HrRequest;
import kz.janapost.digitalhr.request.repository.HrRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HrRequestService {

    private final HrRequestRepository hrRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OneCIntegrationService oneCIntegrationService;
    private final DocumentologIntegrationService documentologIntegrationService;

    public HrRequestDto createRequest(HrRequestCreateRequest request, Long employeeId, Long userId) {
        if (employeeId == null) {
            throw new BadRequestException("Employee ID is not linked to current user account");
        }

        if (request.getType() == RequestType.VACATION) {
            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new BadRequestException("Start date and end date are required for vacation request");
            }
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new BadRequestException("Start date cannot be after end date");
            }
        } else if (request.getType() == RequestType.ADVANCE) {
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Positive amount is required for advance request");
            }
        }

        HrRequest hrRequest = HrRequest.builder()
                .employeeId(employeeId)
                .type(request.getType())
                .status(RequestStatus.PENDING)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .amount(request.getAmount())
                .comment(request.getComment())
                .build();

        HrRequest saved = hrRequestRepository.save(hrRequest);

        notificationService.createNotification(
                userId,
                "Заявка создана",
                "Ваша заявка типа '" + request.getType() + "' успешно зарегистрирована и ожидает согласования.",
                "REQUEST_CREATED",
                saved.getId(),
                "HR_REQUEST"
        );

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<HrRequestDto> getMyRequests(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return hrRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HrRequestDto> getAllRequests() {
        return hrRequestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HrRequestDto getById(Long id) {
        HrRequest request = hrRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HR Request not found with id: " + id));
        return toDto(request);
    }

    public HrRequestDto reviewRequest(Long id, HrRequestReviewRequest reviewRequest, Long reviewerUserId) {
        HrRequest request = hrRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HR Request not found with id: " + id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Request is already in status: " + request.getStatus());
        }

        request.setStatus(reviewRequest.getStatus());
        request.setReviewedBy(reviewerUserId);
        request.setReviewedAt(LocalDateTime.now());
        if (reviewRequest.getStatus() == RequestStatus.REJECTED) {
            request.setRejectionReason(reviewRequest.getRejectionReason());
        }

        HrRequest saved = hrRequestRepository.save(request);

        // Find user for the employee to notify
        Optional<User> employeeUser = userRepository.findAll().stream()
                .filter(u -> saved.getEmployeeId().equals(u.getEmployeeId()))
                .findFirst();

        String statusRu = saved.getStatus() == RequestStatus.APPROVED ? "одобрена" : "отклонена";
        StringBuilder sb = new StringBuilder("Ваша заявка на ").append(saved.getType()).append(" была ").append(statusRu).append(".");
        if (saved.getStatus() == RequestStatus.REJECTED && saved.getRejectionReason() != null) {
            sb.append(" Причина: ").append(saved.getRejectionReason());
        }
        final String notificationMessage = sb.toString();

        employeeUser.ifPresent(user -> notificationService.createNotification(
                user.getId(),
                "Статус заявки обновлён",
                notificationMessage,
                "REQUEST_STATUS_CHANGED",
                saved.getId(),
                "HR_REQUEST"
        ));

        // В случае одобрения - триггерим интеграционные точки с внешними системами:
        if (saved.getStatus() == RequestStatus.APPROVED) {
            oneCIntegrationService.exportOrderTo1C(saved);
            documentologIntegrationService.routeDocumentForSignature(saved);
        }

        return toDto(saved);
    }

    private HrRequestDto toDto(HrRequest req) {
        Integer duration = null;
        if (req.getStartDate() != null && req.getEndDate() != null) {
            duration = (int) ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        }

        String employeeName = employeeRepository.findById(req.getEmployeeId())
                .map(e -> e.getLastName() + " " + e.getFirstName())
                .orElse("Сотрудник #" + req.getEmployeeId());

        String reviewerName = null;
        if (req.getReviewedBy() != null) {
            reviewerName = userRepository.findById(req.getReviewedBy())
                    .map(User::getEmail)
                    .orElse("User #" + req.getReviewedBy());
        }

        return HrRequestDto.builder()
                .id(req.getId())
                .employeeId(req.getEmployeeId())
                .employeeName(employeeName)
                .type(req.getType())
                .status(req.getStatus())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .durationDays(duration)
                .amount(req.getAmount())
                .comment(req.getComment())
                .createdAt(req.getCreatedAt())
                .updatedAt(req.getUpdatedAt())
                .reviewedBy(req.getReviewedBy())
                .reviewerName(reviewerName)
                .reviewedAt(req.getReviewedAt())
                .rejectionReason(req.getRejectionReason())
                .build();
    }
}
