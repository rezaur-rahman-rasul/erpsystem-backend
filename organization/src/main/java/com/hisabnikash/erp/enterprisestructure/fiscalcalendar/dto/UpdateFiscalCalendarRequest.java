package com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto;

import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.domain.FiscalCalendarStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateFiscalCalendarRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private FiscalCalendarStatus status;
}
