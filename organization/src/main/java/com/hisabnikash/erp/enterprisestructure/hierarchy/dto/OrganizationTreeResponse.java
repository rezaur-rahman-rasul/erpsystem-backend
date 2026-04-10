package com.hisabnikash.erp.enterprisestructure.hierarchy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OrganizationTreeResponse {

    private UUID legalEntityId;
    private String legalEntityCode;
    private String legalEntityName;
    private String status;
    private List<BusinessUnitSummaryResponse> businessUnits;
    private List<BranchTreeResponse> branches;
}
