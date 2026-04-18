package com.hishabnikash.erp.organization.settings.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.settings.application.OrganizationSettingService;
import com.hishabnikash.erp.organization.settings.domain.SettingOwnerType;
import com.hishabnikash.erp.organization.settings.dto.OrganizationSettingRequest;
import com.hishabnikash.erp.organization.settings.dto.OrganizationSettingResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class OrganizationSettingController {

    private final OrganizationSettingService organizationSettingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:settings:create')")
    public ApiResponse<OrganizationSettingResponse> create(@Valid @RequestBody OrganizationSettingRequest request) {
        return ApiResponse.success(organizationSettingService.create(request), "Organization settings created");
    }

    @GetMapping("/{ownerType}/{ownerId}")
    @PreAuthorize("hasAuthority('enterprise:settings:view')")
    public ApiResponse<OrganizationSettingResponse> getByOwner(@PathVariable SettingOwnerType ownerType,
                                                               @PathVariable UUID ownerId) {
        return ApiResponse.success(organizationSettingService.getByOwner(ownerType, ownerId));
    }

    @PutMapping("/{ownerType}/{ownerId}")
    @PreAuthorize("hasAuthority('enterprise:settings:update')")
    public ApiResponse<OrganizationSettingResponse> update(@PathVariable SettingOwnerType ownerType,
                                                           @PathVariable UUID ownerId,
                                                           @Valid @RequestBody OrganizationSettingRequest request) {
        return ApiResponse.success(organizationSettingService.update(ownerType, ownerId, request), "Organization settings updated");
    }
}
