package com.hisabnikash.erp.enterprisestructure.fiscalcalendar.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.application.FiscalCalendarService;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.CreateFiscalCalendarRequest;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.FiscalCalendarResponse;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.UpdateFiscalCalendarRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fiscal-calendars")
@RequiredArgsConstructor
public class FiscalCalendarController {

    private final FiscalCalendarService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:fiscal-calendar:create')")
    public ApiResponse<FiscalCalendarResponse> create(@Valid @RequestBody CreateFiscalCalendarRequest request) {
        return ApiResponse.success(service.create(request), "Fiscal calendar created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:fiscal-calendar:view')")
    public ApiResponse<Page<FiscalCalendarResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                            Pageable pageable) {
        return ApiResponse.success(service.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:fiscal-calendar:view')")
    public ApiResponse<FiscalCalendarResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:fiscal-calendar:update')")
    public ApiResponse<FiscalCalendarResponse> update(@PathVariable UUID id,
                                                      @Valid @RequestBody UpdateFiscalCalendarRequest request) {
        return ApiResponse.success(service.update(id, request), "Fiscal calendar updated");
    }
}
