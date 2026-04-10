package com.hisabnikash.erp.enterprisestructure.department.mapper;

import com.hisabnikash.erp.enterprisestructure.department.domain.Department;
import com.hisabnikash.erp.enterprisestructure.department.domain.DepartmentStatus;
import com.hisabnikash.erp.enterprisestructure.department.dto.CreateDepartmentRequest;
import com.hisabnikash.erp.enterprisestructure.department.dto.DepartmentResponse;
import com.hisabnikash.erp.enterprisestructure.department.dto.UpdateDepartmentRequest;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(CreateDepartmentRequest req) {
        Department d = new Department();
        d.setLegalEntityId(req.getLegalEntityId());
        d.setBranchId(req.getBranchId());
        d.setParentDepartmentId(req.getParentDepartmentId());
        d.setCode(req.getCode());
        d.setName(req.getName());
        d.setHeadEmployeeId(req.getHeadEmployeeId());
        d.setStatus(DepartmentStatus.ACTIVE);
        return d;
    }

    public void updateEntity(Department department, UpdateDepartmentRequest request) {
        department.setBranchId(request.getBranchId());
        department.setName(request.getName());
        department.setParentDepartmentId(request.getParentDepartmentId());
        department.setHeadEmployeeId(request.getHeadEmployeeId());
        department.setStatus(request.getStatus());
    }

    public DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .legalEntityId(d.getLegalEntityId())
                .branchId(d.getBranchId())
                .parentDepartmentId(d.getParentDepartmentId())
                .code(d.getCode())
                .name(d.getName())
                .headEmployeeId(d.getHeadEmployeeId())
                .status(d.getStatus().name())
                .createdBy(d.getCreatedBy())
                .createdAt(d.getCreatedAt())
                .lastUpdatedBy(d.getUpdatedBy())
                .lastUpdatedAt(d.getUpdatedAt())
                .build();
    }
}
