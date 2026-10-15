package kz.janapost.digitalhr.hiring.service;

import kz.janapost.digitalhr.common.enums.CandidateStage;
import kz.janapost.digitalhr.common.enums.EmployeeStatus;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.employee.dto.EmployeeDto;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.entity.EmployeeAuditLog;
import kz.janapost.digitalhr.employee.mapper.EmployeeMapper;
import kz.janapost.digitalhr.employee.repository.EmployeeAuditLogRepository;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import kz.janapost.digitalhr.hiring.dto.*;
import kz.janapost.digitalhr.hiring.entity.Candidate;
import kz.janapost.digitalhr.hiring.entity.Vacancy;
import kz.janapost.digitalhr.hiring.mapper.CandidateMapper;
import kz.janapost.digitalhr.hiring.repository.CandidateRepository;
import kz.janapost.digitalhr.hiring.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final VacancyRepository vacancyRepository;
    private final CandidateMapper candidateMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAuditLogRepository auditLogRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<CandidateDto> getByVacancy(Long vacancyId) {
        if (!vacancyRepository.existsById(vacancyId)) {
            throw new ResourceNotFoundException("Vacancy not found with id: " + vacancyId);
        }
        return candidateRepository.findByVacancyId(vacancyId).stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CandidateDto getById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        return candidateMapper.toDto(candidate);
    }

    public CandidateDto create(CandidateCreateRequest request) {
        if (!vacancyRepository.existsById(request.getVacancyId())) {
            throw new ResourceNotFoundException("Vacancy not found with id: " + request.getVacancyId());
        }

        Candidate candidate = candidateMapper.toEntity(request);
        candidate.setStage(CandidateStage.NEW);

        Candidate saved = candidateRepository.save(candidate);
        return candidateMapper.toDto(saved);
    }

    public CandidateDto update(Long id, CandidateUpdateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        if (request.getFirstName() != null) candidate.setFirstName(request.getFirstName());
        if (request.getLastName() != null) candidate.setLastName(request.getLastName());
        if (request.getEmail() != null) candidate.setEmail(request.getEmail());
        if (request.getPhone() != null) candidate.setPhone(request.getPhone());
        if (request.getResumeUrl() != null) candidate.setResumeUrl(request.getResumeUrl());
        if (request.getNotes() != null) candidate.setNotes(request.getNotes());
        if (request.getAssignedTo() != null) candidate.setAssignedTo(request.getAssignedTo());

        Candidate updated = candidateRepository.save(candidate);
        return candidateMapper.toDto(updated);
    }

    public CandidateDto changeStage(Long id, StageChangeRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        candidate.setStage(request.getStage());
        Candidate updated = candidateRepository.save(candidate);
        return candidateMapper.toDto(updated);
    }

    public EmployeeDto hire(Long id, HireCandidateRequest request, Long currentUserId) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        Vacancy vacancy = vacancyRepository.findById(candidate.getVacancyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + candidate.getVacancyId()));

        candidate.setStage(CandidateStage.HIRED);
        candidateRepository.save(candidate);

        Long deptId = request.getDepartmentId() != null ? request.getDepartmentId() : vacancy.getDepartmentId();
        String city = request.getCity() != null ? request.getCity() : vacancy.getCity();

        Employee employee = Employee.builder()
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .position(request.getPosition())
                .departmentId(deptId)
                .city(city)
                .status(EmployeeStatus.ACTIVE)
                .hireDate(request.getHireDate())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .iin(request.getIin())
                .build();

        Employee saved = employeeRepository.save(employee);

        auditLogRepository.save(EmployeeAuditLog.builder()
                .employeeId(saved.getId())
                .fieldName("STATUS")
                .oldValue(null)
                .newValue("HIRED_FROM_CANDIDATE_" + candidate.getId())
                .changedBy(currentUserId)
                .build());

        return employeeMapper.toDto(saved);
    }
}
