package kz.janapost.digitalhr.department.service;

import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.department.dto.DepartmentCreateRequest;
import kz.janapost.digitalhr.department.dto.DepartmentDto;
import kz.janapost.digitalhr.department.dto.DepartmentTreeDto;
import kz.janapost.digitalhr.department.dto.DepartmentUpdateRequest;
import kz.janapost.digitalhr.department.entity.Department;
import kz.janapost.digitalhr.department.mapper.DepartmentMapper;
import kz.janapost.digitalhr.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional(readOnly = true)
    public List<DepartmentTreeDto> getTree() {
        List<Department> allActive = departmentRepository.findAllByIsActiveTrue();
        Map<Long, DepartmentTreeDto> dtoMap = allActive.stream()
                .map(departmentMapper::toTreeDto)
                .collect(Collectors.toMap(DepartmentTreeDto::getId, dto -> dto));

        List<DepartmentTreeDto> rootNodes = new ArrayList<>();

        for (Department d : allActive) {
            DepartmentTreeDto current = dtoMap.get(d.getId());
            Long parentId = d.getParent() != null ? d.getParent().getId() : null;

            if (parentId != null && dtoMap.containsKey(parentId)) {
                dtoMap.get(parentId).getChildren().add(current);
            } else {
                rootNodes.add(current);
            }
        }

        return rootNodes;
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAll() {
        return departmentRepository.findAllByIsActiveTrue().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartmentDto getById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return departmentMapper.toDto(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getChildren(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        return departmentRepository.findByParentId(id).stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public DepartmentDto create(DepartmentCreateRequest request) {
        if (request.getParentId() != null && departmentRepository.existsByNameAndParentId(request.getName(), request.getParentId())) {
            throw new BadRequestException("Department with name '" + request.getName() + "' already exists under this parent");
        }

        Department parent = null;
        int level = 0;
        if (request.getParentId() != null) {
            parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found with id: " + request.getParentId()));
            level = parent.getLevel() != null ? parent.getLevel() + 1 : 1;
        }

        Department department = departmentMapper.toEntity(request);
        department.setParent(parent);
        department.setLevel(level);
        department.setIsActive(true);

        Department saved = departmentRepository.save(department);
        return departmentMapper.toDto(saved);
    }

    public DepartmentDto update(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (request.getName() != null) {
            department.setName(request.getName());
        }
        if (request.getType() != null) {
            department.setType(request.getType());
        }
        if (request.getCity() != null) {
            department.setCity(request.getCity());
        }
        if (request.getIsActive() != null) {
            department.setIsActive(request.getIsActive());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Department cannot be parent of itself");
            }
            Department parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found with id: " + request.getParentId()));
            department.setParent(parent);
            department.setLevel(parent.getLevel() != null ? parent.getLevel() + 1 : 1);
        }

        Department saved = departmentRepository.save(department);
        return departmentMapper.toDto(saved);
    }

    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setIsActive(false);
        departmentRepository.save(department);
    }
}
