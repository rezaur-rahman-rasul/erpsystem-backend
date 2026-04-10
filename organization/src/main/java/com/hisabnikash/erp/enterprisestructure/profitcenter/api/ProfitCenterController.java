package com.hisabnikash.erp.enterprisestructure.profitcenter.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.profitcenter.application.ProfitCenterService;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.CreateProfitCenterRequest;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.ProfitCenterResponse;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.UpdateProfitCenterRequest;
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
@RequestMapping("/api/v1/profit-centers")
@RequiredArgsConstructor
public class ProfitCenterController {

    private final ProfitCenterService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:profit-center:create')")
    public ApiResponse<ProfitCenterResponse> create(@Valid @RequestBody CreateProfitCenterRequest request) {
        return ApiResponse.success(service.create(request), "Profit center created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:profit-center:view')")
    public ApiResponse<Page<ProfitCenterResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                          Pageable pageable) {
        return ApiResponse.success(service.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:profit-center:view')")
    public ApiResponse<ProfitCenterResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:profit-center:update')")
    public ApiResponse<ProfitCenterResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody UpdateProfitCenterRequest request) {
        return ApiResponse.success(service.update(id, request), "Profit center updated");
    }
}
