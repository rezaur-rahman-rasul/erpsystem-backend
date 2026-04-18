package com.hishabnikash.erp.organization.tenantprofile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTenantProfileRequest {

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
