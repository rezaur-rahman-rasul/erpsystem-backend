package com.hisabnikash.erp.masterdata.paymentterm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentTermResponse(
        UUID id,
        String code,
        String name,
        int dueDays,
        Integer discountDays,
        BigDecimal discountPercentage,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
