package com.hishabnikash.erp.organization.hierarchy.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.hierarchy.application.OrganizationHierarchyService;
import com.hishabnikash.erp.organization.hierarchy.dto.HierarchyValidationRequest;
import com.hishabnikash.erp.organization.hierarchy.dto.HierarchyValidationResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.OrganizationTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrganizationHierarchyController {

    private final OrganizationHierarchyService organizationHierarchyService;

    @GetMapping("/api/v1/org-tree")
    @PreAuthorize("hasAuthority('enterprise:hierarchy:view')")
    public ApiResponse<List<OrganizationTreeResponse>> getAllTrees() {
        return ApiResponse.success(organizationHierarchyService.getAllTrees());
    }

    @GetMapping("/api/v1/org-tree/{legalEntityId}")
    @PreAuthorize("hasAuthority('enterprise:hierarchy:view')")
    public ApiResponse<OrganizationTreeResponse> getTree(@PathVariable UUID legalEntityId) {
        return ApiResponse.success(organizationHierarchyService.getTree(legalEntityId));
    }

    @PostMapping("/api/v1/hierarchy/validate")
    @PreAuthorize("hasAuthority('enterprise:hierarchy:view')")
    public ApiResponse<HierarchyValidationResponse> validate(@RequestBody HierarchyValidationRequest request) {
        return ApiResponse.success(organizationHierarchyService.validate(request));
    }
}
