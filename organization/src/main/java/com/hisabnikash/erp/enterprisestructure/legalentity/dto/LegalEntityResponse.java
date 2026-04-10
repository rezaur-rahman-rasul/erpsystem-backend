package com.hisabnikash.erp.enterprisestructure.legalentity.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LegalEntityResponse {

    private UUID id;
    private String code;
    private String legalName;
    private String tradeName;
    private String registrationNumber;
    private String taxNumber;
    private String countryCode;
    private String baseCurrencyCode;
    private Integer fiscalYearStartMonth;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String phone;
    private String email;
    private String website;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
