package com.hisabnikash.erp.identityaccess.organizationaccess.api;

import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import com.hisabnikash.erp.identityaccess.organizationaccess.application.OrganizationAccessService;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.CreateOrganizationAccessRequest;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.UpdateOrganizationAccessRequest;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/organization-access")
@RequiredArgsConstructor
public class OrganizationAccessController {

    private final OrganizationAccessService organizationAccessService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('identity:access:create')")
    public ApiResponse<OrganizationAccessResponse> create(@PathVariable UUID userId,
                                                          @Valid @RequestBody CreateOrganizationAccessRequest request) {
        return ApiResponse.success(organizationAccessService.create(userId, request), "Organization access created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('identity:access:view')")
    public ApiResponse<Set<OrganizationAccessResponse>> getAll(@PathVariable UUID userId) {
        return ApiResponse.success(organizationAccessService.getAllByUserId(userId));
    }

    @PutMapping("/{accessId}")
    @PreAuthorize("hasAuthority('identity:access:update')")
    public ApiResponse<OrganizationAccessResponse> update(@PathVariable UUID userId,
                                                          @PathVariable UUID accessId,
                                                          @Valid @RequestBody UpdateOrganizationAccessRequest request) {
        return ApiResponse.success(organizationAccessService.update(userId, accessId, request), "Organization access updated");
    }
}
