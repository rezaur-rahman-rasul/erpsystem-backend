package com.hisabnikash.erp.identityaccess.permission.api;

import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import com.hisabnikash.erp.identityaccess.permission.application.PermissionCatalogService;
import com.hisabnikash.erp.identityaccess.permission.dto.PermissionDefinitionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionCatalogController {

    private final PermissionCatalogService service;

    @GetMapping
    @PreAuthorize("hasAuthority('identity:permission:view')")
    public ApiResponse<List<PermissionDefinitionResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }
}
