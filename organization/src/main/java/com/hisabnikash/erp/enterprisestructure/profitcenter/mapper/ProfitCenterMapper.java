package com.hisabnikash.erp.enterprisestructure.profitcenter.mapper;

import com.hisabnikash.erp.enterprisestructure.profitcenter.domain.ProfitCenter;
import com.hisabnikash.erp.enterprisestructure.profitcenter.domain.ProfitCenterStatus;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.CreateProfitCenterRequest;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.ProfitCenterResponse;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.UpdateProfitCenterRequest;
import org.springframework.stereotype.Component;

@Component
public class ProfitCenterMapper {

    public ProfitCenter toEntity(CreateProfitCenterRequest request) {
        ProfitCenter profitCenter = new ProfitCenter();
        profitCenter.setLegalEntityId(request.getLegalEntityId());
        profitCenter.setBusinessUnitId(request.getBusinessUnitId());
        profitCenter.setCode(request.getCode());
        profitCenter.setName(request.getName());
        profitCenter.setDescription(request.getDescription());
        profitCenter.setStatus(ProfitCenterStatus.ACTIVE);
        return profitCenter;
    }

    public void updateEntity(ProfitCenter profitCenter, UpdateProfitCenterRequest request) {
        profitCenter.setBusinessUnitId(request.getBusinessUnitId());
        profitCenter.setName(request.getName());
        profitCenter.setDescription(request.getDescription());
        profitCenter.setStatus(request.getStatus());
    }

    public ProfitCenterResponse toResponse(ProfitCenter profitCenter) {
        return ProfitCenterResponse.builder()
                .id(profitCenter.getId())
                .legalEntityId(profitCenter.getLegalEntityId())
                .businessUnitId(profitCenter.getBusinessUnitId())
                .code(profitCenter.getCode())
                .name(profitCenter.getName())
                .description(profitCenter.getDescription())
                .status(profitCenter.getStatus().name())
                .createdBy(profitCenter.getCreatedBy())
                .createdAt(profitCenter.getCreatedAt())
                .lastUpdatedBy(profitCenter.getUpdatedBy())
                .lastUpdatedAt(profitCenter.getUpdatedAt())
                .build();
    }
}
