package kz.janapost.digitalhr.onboarding.mapper;

import kz.janapost.digitalhr.onboarding.dto.*;
import kz.janapost.digitalhr.onboarding.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {

    @Mapping(target = "items", ignore = true)
    OnboardingTemplateDto toDto(OnboardingTemplate template);

    OnboardingTemplateItemDto toItemDto(OnboardingTemplateItem item);

    @Mapping(target = "tasks", ignore = true)
    OnboardingChecklistDto toChecklistDto(OnboardingChecklist checklist);

    OnboardingTaskDto toTaskDto(OnboardingTask task);
}
