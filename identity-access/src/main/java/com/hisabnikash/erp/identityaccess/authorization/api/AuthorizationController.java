package com.hisabnikash.erp.identityaccess.authorization.api;

import com.hisabnikash.erp.identityaccess.authorization.application.AuthorizationCatalogService;
import com.hisabnikash.erp.identityaccess.authorization.application.AuthorizationEvaluationService;
import com.hisabnikash.erp.identityaccess.authorization.dto.AuthorizationActionResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.AuthorizationResourceResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.EffectivePermissionsRequest;
import com.hisabnikash.erp.identityaccess.authorization.dto.EffectivePermissionsResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.PermissionCheckRequest;
import com.hisabnikash.erp.identityaccess.authorization.dto.PermissionCheckResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.ResourcePermissionResponse;
import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authz")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationCatalogService catalogService;
    private final AuthorizationEvaluationService evaluationService;

    @GetMapping("/actions")
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<List<AuthorizationActionResponse>> getActions() {
        return ApiResponse.success(catalogService.getActions());
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<List<ResourcePermissionResponse>> getPermissions() {
        return ApiResponse.success(catalogService.getPermissions());
    }

    @GetMapping("/resources")
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<List<AuthorizationResourceResponse>> getResources(@RequestParam(required = false) String query,
                                                                         @RequestParam(required = false) String serviceCode,
                                                                         @RequestParam(required = false) String type) {
        return ApiResponse.success(catalogService.getResources(query, serviceCode, type));
    }

    @GetMapping("/resources/{fullCode}")
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<AuthorizationResourceResponse> getResource(@PathVariable String fullCode) {
        return ApiResponse.success(catalogService.getResource(fullCode));
    }

    @GetMapping("/resources/{fullCode}/permissions")
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<List<ResourcePermissionResponse>> getPermissionsForResource(@PathVariable String fullCode) {
        return ApiResponse.success(catalogService.getPermissionsForResource(fullCode));
    }

    @PostMapping("/effective-permissions")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<EffectivePermissionsResponse> getEffectivePermissions(@RequestBody(required = false) EffectivePermissionsRequest request) {
        return ApiResponse.success(evaluationService.getEffectivePermissions(request != null ? request.userId() : null));
    }

    @PostMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PermissionCheckResponse> check(@Valid @RequestBody PermissionCheckRequest request) {
        return ApiResponse.success(evaluationService.checkPermission(request.userId(), request.resourceCode(), request.action()));
    }
}
