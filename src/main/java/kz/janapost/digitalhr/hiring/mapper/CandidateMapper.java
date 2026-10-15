package kz.janapost.digitalhr.hiring.mapper;

import kz.janapost.digitalhr.hiring.dto.CandidateCreateRequest;
import kz.janapost.digitalhr.hiring.dto.CandidateDto;
import kz.janapost.digitalhr.hiring.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    CandidateDto toDto(Candidate candidate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Candidate toEntity(CandidateCreateRequest request);
}
