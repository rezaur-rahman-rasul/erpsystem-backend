package com.hisabnikash.erp.identityaccess.role.api;

import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import com.hisabnikash.erp.identityaccess.role.application.RoleService;
import com.hisabnikash.erp.identityaccess.role.dto.CreateRoleRequest;
import com.hisabnikash.erp.identityaccess.role.dto.RoleResponse;
import com.hisabnikash.erp.identityaccess.role.dto.UpdateRoleRequest;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('identity:role:create')")
    public ApiResponse<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ApiResponse.success(service.create(request), "Role created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('identity:role:view')")
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('identity:role:view')")
    public ApiResponse<RoleResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getResponseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('identity:role:update')")
    public ApiResponse<RoleResponse> update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateRoleRequest request) {
        return ApiResponse.success(service.update(id, request), "Role updated");
    }
}
