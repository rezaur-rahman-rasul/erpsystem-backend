package com.hisabnikash.erp.masterdata.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCustomerRequest(
        @NotNull UUID legalEntityId,
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String name,
        @Email @Size(max = 150) String email,
        @Size(max = 50) String phone,
        @Size(max = 100) String taxNumber,
        boolean active
) {
}
