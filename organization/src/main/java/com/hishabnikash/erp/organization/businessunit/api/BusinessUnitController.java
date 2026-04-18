package com.hishabnikash.erp.organization.businessunit.api;

import com.hishabnikash.erp.organization.businessunit.application.BusinessUnitService;
import com.hishabnikash.erp.organization.businessunit.dto.BusinessUnitResponse;
import com.hishabnikash.erp.organization.businessunit.dto.CreateBusinessUnitRequest;
import com.hishabnikash.erp.organization.businessunit.dto.UpdateBusinessUnitRequest;
import com.hishabnikash.erp.organization.common.response.ApiResponse;
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
@RequestMapping("/api/v1/business-units")
@RequiredArgsConstructor
public class BusinessUnitController {

    private final BusinessUnitService businessUnitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:business-unit:create')")
    public ApiResponse<BusinessUnitResponse> create(@Valid @RequestBody CreateBusinessUnitRequest request) {
        return ApiResponse.success(businessUnitService.create(request), "Business unit created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:business-unit:view')")
    public ApiResponse<Page<BusinessUnitResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                          Pageable pageable) {
        return ApiResponse.success(businessUnitService.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:business-unit:view')")
    public ApiResponse<BusinessUnitResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(businessUnitService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:business-unit:update')")
    public ApiResponse<BusinessUnitResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody UpdateBusinessUnitRequest request) {
        return ApiResponse.success(businessUnitService.update(id, request), "Business unit updated");
    }
}
