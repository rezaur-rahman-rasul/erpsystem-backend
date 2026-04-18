package com.hishabnikash.erp.organization.legalentity.mapper;

import com.hishabnikash.erp.organization.legalentity.domain.LegalEntity;
import com.hishabnikash.erp.organization.legalentity.domain.LegalEntityStatus;
import com.hishabnikash.erp.organization.legalentity.dto.CreateLegalEntityRequest;
import com.hishabnikash.erp.organization.legalentity.dto.LegalEntityResponse;
import com.hishabnikash.erp.organization.legalentity.dto.UpdateLegalEntityRequest;
import org.springframework.stereotype.Component;

@Component
public class LegalEntityMapper {

    public LegalEntity toEntity(CreateLegalEntityRequest request) {
        LegalEntity entity = new LegalEntity();
        entity.setCode(request.getCode());
        entity.setLegalName(request.getLegalName());
        entity.setTradeName(request.getTradeName());
        entity.setRegistrationNumber(request.getRegistrationNumber());
        entity.setTaxNumber(request.getTaxNumber());
        entity.setCountryCode(request.getCountryCode());
        entity.setBaseCurrencyCode(request.getBaseCurrencyCode());
        entity.setFiscalYearStartMonth(request.getFiscalYearStartMonth());
        entity.setAddressLine1(request.getAddressLine1());
        entity.setAddressLine2(request.getAddressLine2());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setPostalCode(request.getPostalCode());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setWebsite(request.getWebsite());
        entity.setStatus(LegalEntityStatus.ACTIVE);
        return entity;
    }

    public void updateEntity(LegalEntity entity, UpdateLegalEntityRequest request) {
        entity.setLegalName(request.getLegalName());
        entity.setTradeName(request.getTradeName());
        entity.setRegistrationNumber(request.getRegistrationNumber());
        entity.setTaxNumber(request.getTaxNumber());
        entity.setCountryCode(request.getCountryCode());
        entity.setBaseCurrencyCode(request.getBaseCurrencyCode());
        entity.setFiscalYearStartMonth(request.getFiscalYearStartMonth());
        entity.setAddressLine1(request.getAddressLine1());
        entity.setAddressLine2(request.getAddressLine2());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setPostalCode(request.getPostalCode());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setWebsite(request.getWebsite());
        updateStatus(entity, request.getStatus());
    }

    public void updateStatus(LegalEntity entity, String status) {
        entity.setStatus(LegalEntityStatus.valueOf(status.trim().toUpperCase()));
    }

    public LegalEntityResponse toResponse(LegalEntity entity) {
        return LegalEntityResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .legalName(entity.getLegalName())
                .tradeName(entity.getTradeName())
                .registrationNumber(entity.getRegistrationNumber())
                .taxNumber(entity.getTaxNumber())
                .countryCode(entity.getCountryCode())
                .baseCurrencyCode(entity.getBaseCurrencyCode())
                .fiscalYearStartMonth(entity.getFiscalYearStartMonth())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .status(entity.getStatus().name())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .lastUpdatedBy(entity.getUpdatedBy())
                .lastUpdatedAt(entity.getUpdatedAt())
                .build();
    }
}
