package com.hisabnikash.erp.enterprisestructure.subsidiary.mapper;

import com.hisabnikash.erp.enterprisestructure.subsidiary.domain.Subsidiary;
import com.hisabnikash.erp.enterprisestructure.subsidiary.domain.SubsidiaryStatus;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.CreateSubsidiaryRequest;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.SubsidiaryResponse;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.UpdateSubsidiaryRequest;
import org.springframework.stereotype.Component;

@Component
public class SubsidiaryMapper {

    public Subsidiary toEntity(CreateSubsidiaryRequest request) {
        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setParentLegalEntityId(request.getParentLegalEntityId());
        subsidiary.setLegalEntityId(request.getLegalEntityId());
        subsidiary.setCode(request.getCode());
        subsidiary.setName(request.getName());
        subsidiary.setDescription(request.getDescription());
        subsidiary.setStatus(SubsidiaryStatus.ACTIVE);
        return subsidiary;
    }

    public void updateEntity(Subsidiary subsidiary, UpdateSubsidiaryRequest request) {
        subsidiary.setParentLegalEntityId(request.getParentLegalEntityId());
        subsidiary.setName(request.getName());
        subsidiary.setDescription(request.getDescription());
        subsidiary.setStatus(request.getStatus());
    }

    public SubsidiaryResponse toResponse(Subsidiary subsidiary) {
        return SubsidiaryResponse.builder()
                .id(subsidiary.getId())
                .parentLegalEntityId(subsidiary.getParentLegalEntityId())
                .legalEntityId(subsidiary.getLegalEntityId())
                .code(subsidiary.getCode())
                .name(subsidiary.getName())
                .description(subsidiary.getDescription())
                .status(subsidiary.getStatus().name())
                .createdBy(subsidiary.getCreatedBy())
                .createdAt(subsidiary.getCreatedAt())
                .lastUpdatedBy(subsidiary.getUpdatedBy())
                .lastUpdatedAt(subsidiary.getUpdatedAt())
                .build();
    }
}
