package com.hisabnikash.erp.enterprisestructure.legalentity.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLegalEntityRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String legalName;

    @Size(max = 255)
    private String tradeName;

    @NotBlank
    @Size(max = 100)
    private String registrationNumber;

    @Size(max = 100)
    private String taxNumber;

    @NotBlank
    @Size(max = 10)
    private String countryCode;

    @NotBlank
    @Size(max = 10)
    private String baseCurrencyCode;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer fiscalYearStartMonth;

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

    @Size(max = 50)
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 150)
    private String website;
}