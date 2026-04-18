package com.hishabnikash.erp.organization.settings.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrganizationSettingResponse {

    private UUID id;
    private String ownerType;
    private UUID ownerId;
    private String defaultCurrency;
    private String defaultLanguage;
    private String dateFormat;
    private String timeFormat;
    private String taxRegion;
    private String invoicePrefix;
    private String poPrefix;
    private String employeePrefix;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
