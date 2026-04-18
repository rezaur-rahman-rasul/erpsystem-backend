package com.hishabnikash.erp.organization.fiscalcalendar.dto;

import com.hishabnikash.erp.organization.fiscalcalendar.domain.FiscalCalendarStatus;
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
