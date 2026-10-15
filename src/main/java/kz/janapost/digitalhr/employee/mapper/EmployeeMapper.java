package kz.janapost.digitalhr.employee.mapper;

import kz.janapost.digitalhr.employee.dto.*;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.entity.EmployeeAuditLog;
import kz.janapost.digitalhr.employee.entity.EmployeeDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto toDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);

    EmployeeHistoryDto toHistoryDto(EmployeeAuditLog log);

    DocumentDto toDocumentDto(EmployeeDocument doc);
}
