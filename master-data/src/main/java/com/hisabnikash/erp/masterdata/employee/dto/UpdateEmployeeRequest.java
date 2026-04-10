package com.hisabnikash.erp.masterdata.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateEmployeeRequest(
        @NotNull UUID legalEntityId,
        @NotBlank @Size(max = 40) String employeeNumber,
        @NotBlank @Size(max = 150) String fullName,
        @Email @Size(max = 150) String email,
        @Size(max = 50) String phone,
        @Size(max = 120) String designation,
        boolean active
) {
}
