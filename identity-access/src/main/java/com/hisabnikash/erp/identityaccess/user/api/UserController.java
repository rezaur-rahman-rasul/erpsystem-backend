package com.hisabnikash.erp.identityaccess.user.api;

import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import com.hisabnikash.erp.identityaccess.user.application.UserService;
import com.hisabnikash.erp.identityaccess.user.dto.CreateUserRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UpdateUserRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UpdateUserStatusRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('identity:user:create')")
    public ApiResponse<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(service.create(request), "User created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('identity:user:view')")
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('identity:user:view')")
    public ApiResponse<UserResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getResponseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('identity:user:update')")
    public ApiResponse<UserResponse> update(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(service.update(id, request), "User updated");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('identity:user:update')")
    public ApiResponse<UserResponse> changeStatus(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateUserStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "User status updated");
    }
}
