package com.hishabnikash.erp.organization.businessunit.mapper;

import com.hishabnikash.erp.organization.businessunit.domain.BusinessUnit;
import com.hishabnikash.erp.organization.businessunit.domain.BusinessUnitStatus;
import com.hishabnikash.erp.organization.businessunit.dto.BusinessUnitResponse;
import com.hishabnikash.erp.organization.businessunit.dto.CreateBusinessUnitRequest;
import com.hishabnikash.erp.organization.businessunit.dto.UpdateBusinessUnitRequest;
import org.springframework.stereotype.Component;

@Component
public class BusinessUnitMapper {

    public BusinessUnit toEntity(CreateBusinessUnitRequest request) {
        BusinessUnit businessUnit = new BusinessUnit();
        businessUnit.setLegalEntityId(request.getLegalEntityId());
        businessUnit.setCode(request.getCode());
        businessUnit.setName(request.getName());
        businessUnit.setDescription(request.getDescription());
        businessUnit.setManagerEmployeeId(request.getManagerEmployeeId());
        businessUnit.setStatus(BusinessUnitStatus.ACTIVE);
        return businessUnit;
    }

    public void updateEntity(BusinessUnit businessUnit, UpdateBusinessUnitRequest request) {
        businessUnit.setName(request.getName());
        businessUnit.setDescription(request.getDescription());
        businessUnit.setManagerEmployeeId(request.getManagerEmployeeId());
        businessUnit.setStatus(request.getStatus());
    }

    public BusinessUnitResponse toResponse(BusinessUnit businessUnit) {
        return BusinessUnitResponse.builder()
                .id(businessUnit.getId())
                .legalEntityId(businessUnit.getLegalEntityId())
                .code(businessUnit.getCode())
                .name(businessUnit.getName())
                .description(businessUnit.getDescription())
                .managerEmployeeId(businessUnit.getManagerEmployeeId())
                .status(businessUnit.getStatus().name())
                .createdBy(businessUnit.getCreatedBy())
                .createdAt(businessUnit.getCreatedAt())
                .lastUpdatedBy(businessUnit.getUpdatedBy())
                .lastUpdatedAt(businessUnit.getUpdatedAt())
                .build();
    }
}
