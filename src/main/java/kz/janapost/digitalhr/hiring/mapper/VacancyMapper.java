package kz.janapost.digitalhr.hiring.mapper;

import kz.janapost.digitalhr.hiring.dto.VacancyCreateRequest;
import kz.janapost.digitalhr.hiring.dto.VacancyDto;
import kz.janapost.digitalhr.hiring.entity.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vacancy toEntity(VacancyCreateRequest request);
}
