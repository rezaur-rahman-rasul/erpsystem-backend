package com.hishabnikash.erp.organization.tenantprofile.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.tenantprofile.application.TenantProfileService;
import com.hishabnikash.erp.organization.tenantprofile.dto.CreateTenantProfileRequest;
import com.hishabnikash.erp.organization.tenantprofile.dto.TenantProfileResponse;
import com.hishabnikash.erp.organization.tenantprofile.dto.UpdateTenantProfileRequest;
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
@RequestMapping("/api/v1/tenant-profiles")
@RequiredArgsConstructor
public class TenantProfileController {

    private final TenantProfileService tenantProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:tenant-profile:create')")
    public ApiResponse<TenantProfileResponse> create(@Valid @RequestBody CreateTenantProfileRequest request) {
        return ApiResponse.success(tenantProfileService.create(request), "Tenant profile created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:tenant-profile:view')")
    public ApiResponse<Page<TenantProfileResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                           Pageable pageable) {
        return ApiResponse.success(tenantProfileService.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:tenant-profile:view')")
    public ApiResponse<TenantProfileResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(tenantProfileService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:tenant-profile:update')")
    public ApiResponse<TenantProfileResponse> update(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateTenantProfileRequest request) {
        return ApiResponse.success(tenantProfileService.update(id, request), "Tenant profile updated");
    }
}
