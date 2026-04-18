package com.hishabnikash.erp.organization.fiscalcalendar.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class FiscalCalendarResponse {

    private UUID id;
    private UUID legalEntityId;
    private String code;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
