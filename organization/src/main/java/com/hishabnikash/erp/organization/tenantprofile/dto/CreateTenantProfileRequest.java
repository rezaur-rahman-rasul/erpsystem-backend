package com.hishabnikash.erp.organization.tenantprofile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTenantProfileRequest {

    @NotBlank
    @Size(max = 80)
    private String tenantCode;

    @NotNull
    private UUID legalEntityId;

    @NotBlank
    @Size(max = 255)
    private String companyName;

    @Size(max = 255)
    private String brandName;

    @Email
    @Size(max = 150)
    private String supportEmail;

    @Size(max = 255)
    private String websiteUrl;

    @Size(max = 255)
    private String logoUrl;

    private boolean active;
}
