package com.hishabnikash.erp.organization.location.dto;

import com.hishabnikash.erp.organization.location.domain.LocationStatus;
import com.hishabnikash.erp.organization.location.domain.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateLocationRequest {

    private UUID branchId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private LocationType type;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 50)
    private String postalCode;

    @Size(max = 10)
    private String countryCode;

    @NotNull
    private LocationStatus status;
}
