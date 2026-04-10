package com.hisabnikash.erp.enterprisestructure.tenantprofile.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TenantProfileResponse {

    private UUID id;
    private String tenantCode;
    private UUID legalEntityId;
    private String companyName;
    private String brandName;
    private String supportEmail;
    private String websiteUrl;
    private String logoUrl;
    private boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
