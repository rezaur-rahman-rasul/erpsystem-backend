package com.hishabnikash.erp.organization.department.mapper;

import com.hishabnikash.erp.organization.department.domain.Department;
import com.hishabnikash.erp.organization.department.domain.DepartmentStatus;
import com.hishabnikash.erp.organization.department.dto.CreateDepartmentRequest;
import com.hishabnikash.erp.organization.department.dto.DepartmentResponse;
import com.hishabnikash.erp.organization.department.dto.UpdateDepartmentRequest;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(CreateDepartmentRequest request) {
        Department department = new Department();
        department.setLegalEntityId(request.getLegalEntityId());
        department.setBranchId(request.getBranchId());
        department.setParentDepartmentId(request.getParentDepartmentId());
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setHeadEmployeeId(request.getHeadEmployeeId());
        department.setStatus(DepartmentStatus.ACTIVE);
        return department;
    }

    public void updateEntity(Department department, UpdateDepartmentRequest request) {
        department.setBranchId(request.getBranchId());
        department.setName(request.getName());
        department.setParentDepartmentId(request.getParentDepartmentId());
        department.setHeadEmployeeId(request.getHeadEmployeeId());
        department.setStatus(request.getStatus());
    }

    public DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .legalEntityId(department.getLegalEntityId())
                .branchId(department.getBranchId())
                .parentDepartmentId(department.getParentDepartmentId())
                .code(department.getCode())
                .name(department.getName())
                .headEmployeeId(department.getHeadEmployeeId())
                .status(department.getStatus().name())
                .createdBy(department.getCreatedBy())
                .createdAt(department.getCreatedAt())
                .lastUpdatedBy(department.getUpdatedBy())
                .lastUpdatedAt(department.getUpdatedAt())
                .build();
    }
}
