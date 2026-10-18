package kz.janapost.digitalhr.employee.service;

import jakarta.persistence.criteria.Predicate;
import kz.janapost.digitalhr.common.dto.PageResponse;
import kz.janapost.digitalhr.common.enums.EmployeeStatus;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.employee.dto.*;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.entity.EmployeeAuditLog;
import kz.janapost.digitalhr.employee.entity.EmployeeDocument;
import kz.janapost.digitalhr.employee.mapper.EmployeeMapper;
import kz.janapost.digitalhr.employee.repository.EmployeeAuditLogRepository;
import kz.janapost.digitalhr.employee.repository.EmployeeDocumentRepository;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAuditLogRepository auditLogRepository;
    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeMapper employeeMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<EmployeeDto> getAll(String search, Long departmentId, String city, String status, int page, int size, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.ASC, "lastName");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sortObj = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Employee> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate searchPred = cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("position")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                );
                predicates.add(searchPred);
            }

            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase().trim()));
            }

            if (status != null && !status.isBlank()) {
                try {
                    EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("status"), employeeStatus));
                } catch (IllegalArgumentException ignored) {
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        return PageResponse.of(employeePage.map(employeeMapper::toDto));
    }

    @Transactional(readOnly = true)
    public EmployeeDto getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto create(EmployeeCreateRequest request, Long currentUserId) {
        Employee employee = employeeMapper.toEntity(request);
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }
        Employee saved = employeeRepository.save(employee);

        auditLogRepository.save(EmployeeAuditLog.builder()
                .employeeId(saved.getId())
                .fieldName("STATUS")
                .oldValue(null)
                .newValue("CREATED")
                .changedBy(currentUserId)
                .build());

        // Automatically create user login account for the new employee with their phone number as password
        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            String cleanEmail = saved.getEmail().trim().toLowerCase();
            String defaultPassword = (saved.getPhone() != null && !saved.getPhone().isBlank())
                    ? saved.getPhone().trim()
                    : "janapost123";

            userRepository.findByEmail(cleanEmail).ifPresentOrElse(
                    existingUser -> {
                        existingUser.setEmployeeId(saved.getId());
                        if (saved.getDepartmentId() != null) {
                            existingUser.setDepartmentId(saved.getDepartmentId());
                        }
                        if (existingUser.getPasswordHash() == null || existingUser.getPasswordHash().isBlank()) {
                            existingUser.setPasswordHash(passwordEncoder.encode(defaultPassword));
                        }
                        userRepository.save(existingUser);
                        log.info("Linked existing user {} to new employee #{}", cleanEmail, saved.getId());
                    },
                    () -> {
                        User newUser = User.builder()
                                .email(cleanEmail)
                                .passwordHash(passwordEncoder.encode(defaultPassword))
                                .role(Role.EMPLOYEE)
                                .departmentId(saved.getDepartmentId())
                                .employeeId(saved.getId())
                                .isActive(true)
                                .build();
                        userRepository.save(newUser);
                        log.info("Created user account for new employee #{}: email={}, default password is phone number",
                                saved.getId(), cleanEmail);
                    }
            );
        }

        return employeeMapper.toDto(saved);
    }

    public EmployeeDto update(Long id, EmployeeUpdateRequest request, Long currentUserId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        checkAndLogChange(employee.getId(), "firstName", employee.getFirstName(), request.getFirstName(), currentUserId);
        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());

        checkAndLogChange(employee.getId(), "lastName", employee.getLastName(), request.getLastName(), currentUserId);
        if (request.getLastName() != null) employee.setLastName(request.getLastName());

        checkAndLogChange(employee.getId(), "middleName", employee.getMiddleName(), request.getMiddleName(), currentUserId);
        if (request.getMiddleName() != null) employee.setMiddleName(request.getMiddleName());

        checkAndLogChange(employee.getId(), "position", employee.getPosition(), request.getPosition(), currentUserId);
        if (request.getPosition() != null) employee.setPosition(request.getPosition());

        if (request.getDepartmentId() != null) {
            checkAndLogChange(employee.getId(), "departmentId", String.valueOf(employee.getDepartmentId()), String.valueOf(request.getDepartmentId()), currentUserId);
            employee.setDepartmentId(request.getDepartmentId());
        }

        checkAndLogChange(employee.getId(), "city", employee.getCity(), request.getCity(), currentUserId);
        if (request.getCity() != null) employee.setCity(request.getCity());

        if (request.getStatus() != null) {
            checkAndLogChange(employee.getId(), "status", employee.getStatus() != null ? employee.getStatus().name() : null, request.getStatus().name(), currentUserId);
            employee.setStatus(request.getStatus());
        }

        if (request.getHireDate() != null) {
            checkAndLogChange(employee.getId(), "hireDate", String.valueOf(employee.getHireDate()), String.valueOf(request.getHireDate()), currentUserId);
            employee.setHireDate(request.getHireDate());
        }

        if (request.getTerminationDate() != null) {
            checkAndLogChange(employee.getId(), "terminationDate", String.valueOf(employee.getTerminationDate()), String.valueOf(request.getTerminationDate()), currentUserId);
            employee.setTerminationDate(request.getTerminationDate());
        }

        checkAndLogChange(employee.getId(), "email", employee.getEmail(), request.getEmail(), currentUserId);
        if (request.getEmail() != null) employee.setEmail(request.getEmail());

        checkAndLogChange(employee.getId(), "phone", employee.getPhone(), request.getPhone(), currentUserId);
        if (request.getPhone() != null) employee.setPhone(request.getPhone());

        if (request.getBirthDate() != null) {
            checkAndLogChange(employee.getId(), "birthDate", String.valueOf(employee.getBirthDate()), String.valueOf(request.getBirthDate()), currentUserId);
            employee.setBirthDate(request.getBirthDate());
        }

        checkAndLogChange(employee.getId(), "iin", employee.getIin(), request.getIin(), currentUserId);
        if (request.getIin() != null) employee.setIin(request.getIin());

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toDto(updated);
    }

    public void terminate(Long id, Long currentUserId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        String oldStatus = employee.getStatus() != null ? employee.getStatus().name() : null;
        employee.setStatus(EmployeeStatus.TERMINATED);
        employee.setTerminationDate(LocalDate.now());

        auditLogRepository.save(EmployeeAuditLog.builder()
                .employeeId(employee.getId())
                .fieldName("status")
                .oldValue(oldStatus)
                .newValue(EmployeeStatus.TERMINATED.name())
                .changedBy(currentUserId)
                .build());

        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeHistoryDto> getHistory(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        return auditLogRepository.findByEmployeeIdOrderByChangedAtDesc(id).stream()
                .map(employeeMapper::toHistoryDto)
                .collect(Collectors.toList());
    }

    public DocumentDto addDocument(Long employeeId, DocumentAddRequest request) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        EmployeeDocument doc = EmployeeDocument.builder()
                .employeeId(employeeId)
                .documentType(request.getDocumentType())
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .build();

        EmployeeDocument saved = documentRepository.save(doc);
        return employeeMapper.toDocumentDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocuments(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return documentRepository.findByEmployeeId(employeeId).stream()
                .map(employeeMapper::toDocumentDto)
                .collect(Collectors.toList());
    }

    private void checkAndLogChange(Long employeeId, String field, String oldVal, String newVal, Long userId) {
        if (newVal != null && !Objects.equals(oldVal, newVal)) {
            auditLogRepository.save(EmployeeAuditLog.builder()
                    .employeeId(employeeId)
                    .fieldName(field)
                    .oldValue(oldVal)
                    .newValue(newVal)
                    .changedBy(userId)
                    .build());
        }
    }
}
