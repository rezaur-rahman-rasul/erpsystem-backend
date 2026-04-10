package com.hisabnikash.erp.identityaccess.auth.api;

import com.hisabnikash.erp.identityaccess.auth.application.AuthService;
import com.hisabnikash.erp.identityaccess.auth.dto.LoginRequest;
import com.hisabnikash.erp.identityaccess.auth.dto.LoginResponse;
import com.hisabnikash.erp.identityaccess.auth.dto.LogoutRequest;
import com.hisabnikash.erp.identityaccess.auth.dto.RefreshTokenRequest;
import com.hisabnikash.erp.identityaccess.common.response.ApiResponse;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(service.login(request), "Authentication successful");
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(service.refresh(request.refreshToken()), "Token refreshed");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        service.logout(request.refreshToken());
        return ApiResponse.success(null, "Logged out");
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success(service.getCurrentUser());
    }
}
