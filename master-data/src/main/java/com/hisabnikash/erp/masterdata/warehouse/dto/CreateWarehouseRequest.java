package com.hisabnikash.erp.masterdata.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateWarehouseRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String name,
        @NotNull UUID branchId,
        @Size(max = 50) String locationCode,
        boolean active
) {
}
