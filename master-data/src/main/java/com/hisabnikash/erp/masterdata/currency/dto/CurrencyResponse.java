package com.hisabnikash.erp.masterdata.currency.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CurrencyResponse(
        UUID id,
        String code,
        String name,
        String symbol,
        int decimalPlaces,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
