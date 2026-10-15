package kz.janapost.digitalhr.department.mapper;

import kz.janapost.digitalhr.department.dto.DepartmentCreateRequest;
import kz.janapost.digitalhr.department.dto.DepartmentDto;
import kz.janapost.digitalhr.department.dto.DepartmentTreeDto;
import kz.janapost.digitalhr.department.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "parentId", source = "parent.id")
    DepartmentDto toDto(Department department);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    DepartmentTreeDto toTreeDto(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department toEntity(DepartmentCreateRequest request);
}
