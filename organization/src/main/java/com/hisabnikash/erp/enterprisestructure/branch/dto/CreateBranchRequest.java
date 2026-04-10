package com.hisabnikash.erp.enterprisestructure.branch.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBranchRequest {

    @NotNull
    private UUID legalEntityId;

    private UUID businessUnitId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

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

    @Size(max = 50)
    private String phone;
    @Email
    @Size(max = 150)
    private String email;
    @Size(max = 50)
    private String timezone;
}
