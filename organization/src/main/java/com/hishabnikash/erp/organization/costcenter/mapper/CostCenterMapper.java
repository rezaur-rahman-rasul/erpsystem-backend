package com.hishabnikash.erp.organization.costcenter.mapper;

import com.hishabnikash.erp.organization.costcenter.domain.CostCenter;
import com.hishabnikash.erp.organization.costcenter.domain.CostCenterStatus;
import com.hishabnikash.erp.organization.costcenter.dto.CostCenterResponse;
import com.hishabnikash.erp.organization.costcenter.dto.CreateCostCenterRequest;
import com.hishabnikash.erp.organization.costcenter.dto.UpdateCostCenterRequest;
import org.springframework.stereotype.Component;

@Component
public class CostCenterMapper {

    public CostCenter toEntity(CreateCostCenterRequest request) {
        CostCenter costCenter = new CostCenter();
        costCenter.setLegalEntityId(request.getLegalEntityId());
        costCenter.setDepartmentId(request.getDepartmentId());
        costCenter.setCode(request.getCode());
        costCenter.setName(request.getName());
        costCenter.setDescription(request.getDescription());
        costCenter.setStatus(CostCenterStatus.ACTIVE);
        return costCenter;
    }

    public void updateEntity(CostCenter costCenter, UpdateCostCenterRequest request) {
        costCenter.setDepartmentId(request.getDepartmentId());
        costCenter.setName(request.getName());
        costCenter.setDescription(request.getDescription());
        costCenter.setStatus(request.getStatus());
    }

    public CostCenterResponse toResponse(CostCenter costCenter) {
        return CostCenterResponse.builder()
                .id(costCenter.getId())
                .legalEntityId(costCenter.getLegalEntityId())
                .departmentId(costCenter.getDepartmentId())
                .code(costCenter.getCode())
                .name(costCenter.getName())
                .description(costCenter.getDescription())
                .status(costCenter.getStatus().name())
                .createdBy(costCenter.getCreatedBy())
                .createdAt(costCenter.getCreatedAt())
                .lastUpdatedBy(costCenter.getUpdatedBy())
                .lastUpdatedAt(costCenter.getUpdatedAt())
                .build();
    }
}
