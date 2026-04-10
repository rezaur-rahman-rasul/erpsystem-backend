package com.hisabnikash.erp.enterprisestructure.settings.mapper;

import com.hisabnikash.erp.enterprisestructure.settings.domain.OrganizationSetting;
import com.hisabnikash.erp.enterprisestructure.settings.dto.OrganizationSettingRequest;
import com.hisabnikash.erp.enterprisestructure.settings.dto.OrganizationSettingResponse;
import org.springframework.stereotype.Component;

@Component
public class OrganizationSettingMapper {

    public OrganizationSetting toEntity(OrganizationSettingRequest request) {
        OrganizationSetting setting = new OrganizationSetting();
        updateEntity(setting, request);
        return setting;
    }

    public void updateEntity(OrganizationSetting setting, OrganizationSettingRequest request) {
        setting.setOwnerType(request.getOwnerType());
        setting.setOwnerId(request.getOwnerId());
        setting.setDefaultCurrency(request.getDefaultCurrency());
        setting.setDefaultLanguage(request.getDefaultLanguage());
        setting.setDateFormat(request.getDateFormat());
        setting.setTimeFormat(request.getTimeFormat());
        setting.setTaxRegion(request.getTaxRegion());
        setting.setInvoicePrefix(request.getInvoicePrefix());
        setting.setPoPrefix(request.getPoPrefix());
        setting.setEmployeePrefix(request.getEmployeePrefix());
    }

    public OrganizationSettingResponse toResponse(OrganizationSetting setting) {
        return OrganizationSettingResponse.builder()
                .id(setting.getId())
                .ownerType(setting.getOwnerType().name())
                .ownerId(setting.getOwnerId())
                .defaultCurrency(setting.getDefaultCurrency())
                .defaultLanguage(setting.getDefaultLanguage())
                .dateFormat(setting.getDateFormat())
                .timeFormat(setting.getTimeFormat())
                .taxRegion(setting.getTaxRegion())
                .invoicePrefix(setting.getInvoicePrefix())
                .poPrefix(setting.getPoPrefix())
                .employeePrefix(setting.getEmployeePrefix())
                .createdBy(setting.getCreatedBy())
                .createdAt(setting.getCreatedAt())
                .lastUpdatedBy(setting.getUpdatedBy())
                .lastUpdatedAt(setting.getUpdatedAt())
                .build();
    }
}
