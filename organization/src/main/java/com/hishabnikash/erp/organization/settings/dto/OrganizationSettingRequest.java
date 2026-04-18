package com.hishabnikash.erp.organization.settings.dto;

import com.hishabnikash.erp.organization.settings.domain.SettingOwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganizationSettingRequest {

    @NotNull
    private SettingOwnerType ownerType;

    @NotNull
    private UUID ownerId;

    @Size(max = 10)
    private String defaultCurrency;

    @Size(max = 20)
    private String defaultLanguage;

    @Size(max = 30)
    private String dateFormat;

    @Size(max = 30)
    private String timeFormat;

    @Size(max = 50)
    private String taxRegion;

    @Size(max = 30)
    private String invoicePrefix;

    @Size(max = 30)
    private String poPrefix;

    @Size(max = 30)
    private String employeePrefix;
}
