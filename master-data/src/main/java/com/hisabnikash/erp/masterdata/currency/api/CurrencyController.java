package com.hisabnikash.erp.masterdata.currency.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.currency.application.CurrencyService;
import com.hisabnikash.erp.masterdata.currency.dto.CreateCurrencyRequest;
import com.hisabnikash.erp.masterdata.currency.dto.CurrencyResponse;
import com.hisabnikash.erp.masterdata.currency.dto.UpdateCurrencyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:currency:create')")
    public ApiResponse<CurrencyResponse> create(@Valid @RequestBody CreateCurrencyRequest request) {
        return ApiResponse.success(currencyService.create(request), "Currency created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:currency:view')")
    public ApiResponse<List<CurrencyResponse>> getAll() {
        return ApiResponse.success(currencyService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:currency:view')")
    public ApiResponse<CurrencyResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(currencyService.toResponse(currencyService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:currency:update')")
    public ApiResponse<CurrencyResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateCurrencyRequest request) {
        return ApiResponse.success(currencyService.update(id, request), "Currency updated");
    }
}
