package com.hishabnikash.erp.organization.tenantprofile.mapper;

import com.hishabnikash.erp.organization.tenantprofile.domain.TenantProfile;
import com.hishabnikash.erp.organization.tenantprofile.dto.CreateTenantProfileRequest;
import com.hishabnikash.erp.organization.tenantprofile.dto.TenantProfileResponse;
import com.hishabnikash.erp.organization.tenantprofile.dto.UpdateTenantProfileRequest;
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
