package kz.janapost.digitalhr.hiring.service;

import jakarta.persistence.criteria.Predicate;
import kz.janapost.digitalhr.common.dto.PageResponse;
import kz.janapost.digitalhr.common.enums.VacancyStatus;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.hiring.dto.VacancyCreateRequest;
import kz.janapost.digitalhr.hiring.dto.VacancyDto;
import kz.janapost.digitalhr.hiring.dto.VacancyUpdateRequest;
import kz.janapost.digitalhr.hiring.entity.Vacancy;
import kz.janapost.digitalhr.hiring.mapper.VacancyMapper;
import kz.janapost.digitalhr.hiring.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;

    @Transactional(readOnly = true)
    public PageResponse<VacancyDto> getAll(String status, Long departmentId, String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Vacancy> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                try {
                    VacancyStatus vacancyStatus = VacancyStatus.valueOf(status.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("status"), vacancyStatus));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase().trim()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Vacancy> vacancyPage = vacancyRepository.findAll(spec, pageable);
        return PageResponse.of(vacancyPage.map(vacancyMapper::toDto));
    }

    @Transactional(readOnly = true)
    public VacancyDto getById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));
        return vacancyMapper.toDto(vacancy);
    }

    public VacancyDto create(VacancyCreateRequest request, Long currentUserId) {
        Vacancy vacancy = vacancyMapper.toEntity(request);
        vacancy.setCreatedBy(currentUserId);
        vacancy.setStatus(VacancyStatus.OPEN);

        Vacancy saved = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(saved);
    }

    public VacancyDto update(Long id, VacancyUpdateRequest request) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));

        if (request.getTitle() != null) {
            vacancy.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            vacancy.setDescription(request.getDescription());
        }
        if (request.getDepartmentId() != null) {
            vacancy.setDepartmentId(request.getDepartmentId());
        }
        if (request.getCity() != null) {
            vacancy.setCity(request.getCity());
        }
        if (request.getStatus() != null) {
            vacancy.setStatus(request.getStatus());
        }
        if (request.getRequirements() != null) {
            vacancy.setRequirements(request.getRequirements());
        }
        if (request.getSalaryRange() != null) {
            vacancy.setSalaryRange(request.getSalaryRange());
        }

        Vacancy updated = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(updated);
    }

    public void close(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);
    }

    public void delete(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));
        try {
            vacancyRepository.delete(vacancy);
            vacancyRepository.flush();
        } catch (Exception e) {
            vacancy.setStatus(VacancyStatus.CLOSED);
            vacancyRepository.save(vacancy);
        }
    }
}
