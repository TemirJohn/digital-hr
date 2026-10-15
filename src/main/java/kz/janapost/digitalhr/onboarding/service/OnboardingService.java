package kz.janapost.digitalhr.onboarding.service;

import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.common.exception.ResourceNotFoundException;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import kz.janapost.digitalhr.onboarding.dto.*;
import kz.janapost.digitalhr.onboarding.entity.*;
import kz.janapost.digitalhr.onboarding.mapper.OnboardingMapper;
import kz.janapost.digitalhr.onboarding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final OnboardingTemplateRepository templateRepository;
    private final OnboardingTemplateItemRepository itemRepository;
    private final OnboardingChecklistRepository checklistRepository;
    private final OnboardingTaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final OnboardingMapper onboardingMapper;

    @Transactional(readOnly = true)
    public List<OnboardingTemplateDto> getTemplates() {
        return templateRepository.findAll().stream()
                .map(this::enrichTemplateDto)
                .collect(Collectors.toList());
    }

    public OnboardingTemplateDto createTemplate(OnboardingTemplateCreateRequest request, Long currentUserId) {
        OnboardingTemplate template = OnboardingTemplate.builder()
                .name(request.getName())
                .departmentId(request.getDepartmentId())
                .createdBy(currentUserId)
                .build();

        OnboardingTemplate saved = templateRepository.save(template);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            int idx = 1;
            for (OnboardingTemplateCreateRequest.ItemRequest itemReq : request.getItems()) {
                OnboardingTemplateItem item = OnboardingTemplateItem.builder()
                        .templateId(saved.getId())
                        .title(itemReq.getTitle())
                        .description(itemReq.getDescription())
                        .orderIndex(itemReq.getOrderIndex() != null ? itemReq.getOrderIndex() : idx++)
                        .build();
                itemRepository.save(item);
            }
        }

        return enrichTemplateDto(saved);
    }

    public OnboardingTemplateDto updateTemplate(Long id, OnboardingTemplateCreateRequest request) {
        OnboardingTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding template not found with id: " + id));

        template.setName(request.getName());
        template.setDepartmentId(request.getDepartmentId());
        templateRepository.save(template);

        itemRepository.deleteByTemplateId(id);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            int idx = 1;
            for (OnboardingTemplateCreateRequest.ItemRequest itemReq : request.getItems()) {
                OnboardingTemplateItem item = OnboardingTemplateItem.builder()
                        .templateId(id)
                        .title(itemReq.getTitle())
                        .description(itemReq.getDescription())
                        .orderIndex(itemReq.getOrderIndex() != null ? itemReq.getOrderIndex() : idx++)
                        .build();
                itemRepository.save(item);
            }
        }

        return enrichTemplateDto(template);
    }

    @Transactional(readOnly = true)
    public OnboardingChecklistDto getEmployeeChecklist(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        OnboardingChecklist checklist = checklistRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("No onboarding checklist found for employee id: " + employeeId));

        return enrichChecklistDto(checklist);
    }

    public OnboardingChecklistDto createChecklist(ChecklistCreateRequest request) {
        if (!employeeRepository.existsById(request.getEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId());
        }

        OnboardingTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + request.getTemplateId()));

        if (checklistRepository.existsByEmployeeIdAndStatus(request.getEmployeeId(), "IN_PROGRESS")) {
            throw new BadRequestException("Employee already has an active onboarding checklist");
        }

        OnboardingChecklist checklist = OnboardingChecklist.builder()
                .employeeId(request.getEmployeeId())
                .templateId(template.getId())
                .status("IN_PROGRESS")
                .build();

        OnboardingChecklist savedChecklist = checklistRepository.save(checklist);

        List<OnboardingTemplateItem> items = itemRepository.findByTemplateIdOrderByOrderIndexAsc(template.getId());
        List<OnboardingTask> tasks = new ArrayList<>();

        for (OnboardingTemplateItem item : items) {
            OnboardingTask task = OnboardingTask.builder()
                    .checklistId(savedChecklist.getId())
                    .templateItemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .isCompleted(false)
                    .build();
            tasks.add(taskRepository.save(task));
        }

        OnboardingChecklistDto dto = onboardingMapper.toChecklistDto(savedChecklist);
        dto.setTasks(tasks.stream().map(onboardingMapper::toTaskDto).collect(Collectors.toList()));
        return dto;
    }

    public OnboardingTaskDto completeTask(Long taskId) {
        OnboardingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding task not found with id: " + taskId));

        task.setIsCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        OnboardingTask updatedTask = taskRepository.save(task);

        List<OnboardingTask> allTasks = taskRepository.findByChecklistId(task.getChecklistId());
        boolean allDone = allTasks.stream().allMatch(t -> Boolean.TRUE.equals(t.getIsCompleted()));

        if (allDone) {
            checklistRepository.findById(task.getChecklistId()).ifPresent(cl -> {
                cl.setStatus("COMPLETED");
                cl.setCompletedAt(LocalDateTime.now());
                checklistRepository.save(cl);
            });
        }

        return onboardingMapper.toTaskDto(updatedTask);
    }

    private OnboardingTemplateDto enrichTemplateDto(OnboardingTemplate template) {
        OnboardingTemplateDto dto = onboardingMapper.toDto(template);
        List<OnboardingTemplateItemDto> itemDtos = itemRepository.findByTemplateIdOrderByOrderIndexAsc(template.getId()).stream()
                .map(onboardingMapper::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);
        return dto;
    }

    private OnboardingChecklistDto enrichChecklistDto(OnboardingChecklist checklist) {
        OnboardingChecklistDto dto = onboardingMapper.toChecklistDto(checklist);
        List<OnboardingTaskDto> taskDtos = taskRepository.findByChecklistId(checklist.getId()).stream()
                .map(onboardingMapper::toTaskDto)
                .collect(Collectors.toList());
        dto.setTasks(taskDtos);
        return dto;
    }
}
