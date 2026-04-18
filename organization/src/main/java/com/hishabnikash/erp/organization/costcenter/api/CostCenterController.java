package com.hishabnikash.erp.organization.costcenter.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.costcenter.application.CostCenterService;
import com.hishabnikash.erp.organization.costcenter.dto.CostCenterResponse;
import com.hishabnikash.erp.organization.costcenter.dto.CreateCostCenterRequest;
import com.hishabnikash.erp.organization.costcenter.dto.UpdateCostCenterRequest;
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
@RequestMapping("/api/v1/cost-centers")
@RequiredArgsConstructor
public class CostCenterController {

    private final CostCenterService costCenterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:cost-center:create')")
    public ApiResponse<CostCenterResponse> create(@Valid @RequestBody CreateCostCenterRequest request) {
        return ApiResponse.success(costCenterService.create(request), "Cost center created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:cost-center:view')")
    public ApiResponse<Page<CostCenterResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                        Pageable pageable) {
        return ApiResponse.success(costCenterService.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:cost-center:view')")
    public ApiResponse<CostCenterResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(costCenterService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:cost-center:update')")
    public ApiResponse<CostCenterResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateCostCenterRequest request) {
        return ApiResponse.success(costCenterService.update(id, request), "Cost center updated");
    }
}
