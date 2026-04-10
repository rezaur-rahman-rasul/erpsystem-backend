package com.hisabnikash.erp.enterprisestructure.tenantprofile.mapper;

import com.hisabnikash.erp.enterprisestructure.tenantprofile.domain.TenantProfile;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.CreateTenantProfileRequest;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.TenantProfileResponse;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.UpdateTenantProfileRequest;
import org.springframework.stereotype.Component;

@Component
public class TenantProfileMapper {

    public TenantProfile toEntity(CreateTenantProfileRequest request) {
        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setTenantCode(request.getTenantCode());
        tenantProfile.setLegalEntityId(request.getLegalEntityId());
        tenantProfile.setCompanyName(request.getCompanyName());
        tenantProfile.setBrandName(request.getBrandName());
        tenantProfile.setSupportEmail(request.getSupportEmail());
        tenantProfile.setWebsiteUrl(request.getWebsiteUrl());
        tenantProfile.setLogoUrl(request.getLogoUrl());
        tenantProfile.setActive(request.isActive());
        return tenantProfile;
    }

    public void updateEntity(TenantProfile tenantProfile, UpdateTenantProfileRequest request) {
        tenantProfile.setCompanyName(request.getCompanyName());
        tenantProfile.setBrandName(request.getBrandName());
        tenantProfile.setSupportEmail(request.getSupportEmail());
        tenantProfile.setWebsiteUrl(request.getWebsiteUrl());
        tenantProfile.setLogoUrl(request.getLogoUrl());
        tenantProfile.setActive(request.isActive());
    }

    public TenantProfileResponse toResponse(TenantProfile tenantProfile) {
        return TenantProfileResponse.builder()
                .id(tenantProfile.getId())
                .tenantCode(tenantProfile.getTenantCode())
                .legalEntityId(tenantProfile.getLegalEntityId())
                .companyName(tenantProfile.getCompanyName())
                .brandName(tenantProfile.getBrandName())
                .supportEmail(tenantProfile.getSupportEmail())
                .websiteUrl(tenantProfile.getWebsiteUrl())
                .logoUrl(tenantProfile.getLogoUrl())
                .active(tenantProfile.isActive())
                .createdBy(tenantProfile.getCreatedBy())
                .createdAt(tenantProfile.getCreatedAt())
                .lastUpdatedBy(tenantProfile.getUpdatedBy())
                .lastUpdatedAt(tenantProfile.getUpdatedAt())
                .build();
    }
}
