package com.hisabnikash.erp.masterdata.taxcode.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.taxcode.application.TaxCodeService;
import com.hisabnikash.erp.masterdata.taxcode.dto.CreateTaxCodeRequest;
import com.hisabnikash.erp.masterdata.taxcode.dto.TaxCodeResponse;
import com.hisabnikash.erp.masterdata.taxcode.dto.UpdateTaxCodeRequest;
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
@RequestMapping("/api/v1/tax-codes")
@RequiredArgsConstructor
public class TaxCodeController {

    private final TaxCodeService taxCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:tax-code:create')")
    public ApiResponse<TaxCodeResponse> create(@Valid @RequestBody CreateTaxCodeRequest request) {
        return ApiResponse.success(taxCodeService.create(request), "Tax code created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:tax-code:view')")
    public ApiResponse<List<TaxCodeResponse>> getAll() {
        return ApiResponse.success(taxCodeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:tax-code:view')")
    public ApiResponse<TaxCodeResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(taxCodeService.toResponse(taxCodeService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:tax-code:update')")
    public ApiResponse<TaxCodeResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateTaxCodeRequest request) {
        return ApiResponse.success(taxCodeService.update(id, request), "Tax code updated");
    }
}
