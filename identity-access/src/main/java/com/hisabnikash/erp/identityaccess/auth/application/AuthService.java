package com.hisabnikash.erp.identityaccess.auth.application;

import com.hisabnikash.erp.identityaccess.auth.domain.RefreshToken;
import com.hisabnikash.erp.identityaccess.auth.dto.LoginRequest;
import com.hisabnikash.erp.identityaccess.auth.dto.LoginResponse;
import com.hisabnikash.erp.identityaccess.auth.infrastructure.RefreshTokenRepository;
import com.hisabnikash.erp.identityaccess.common.exception.UnauthorizedException;
import com.hisabnikash.erp.identityaccess.common.util.SecurityUtils;
import com.hisabnikash.erp.identityaccess.config.properties.JwtProperties;
import com.hisabnikash.erp.identityaccess.security.jwt.JwtTokenGenerator;
import com.hisabnikash.erp.identityaccess.user.application.UserService;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserAccountRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {
        UserAccount user = resolveUser(request.identifier());
        validateLogin(request, user);

        refreshTokenRepository.findByUser_IdAndRevokedFalse(user.getId())
                .forEach(token -> token.setRevoked(true));

        String refreshToken = issueRefreshToken(user);
        return buildResponse(user, refreshToken);
    }

    public LoginResponse refresh(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        UserAccount user = refreshToken.getUser();
        ensureUserIsActive(user);

        refreshToken.setRevoked(true);
        String nextRefreshToken = issueRefreshToken(user);
        return buildResponse(user, nextRefreshToken);
    }

    public void logout(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue)
                .ifPresent(token -> token.setRevoked(true));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userService.getResponseById(userId);
    }

    private UserAccount resolveUser(String identifier) {
        return userRepository.findByUsernameIgnoreCase(identifier.trim())
                .or(() -> userRepository.findByEmailIgnoreCase(identifier.trim()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password"));
    }

    private void validateLogin(LoginRequest request, UserAccount user) {
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username/email or password");
        }
        if (request.tenantId() != null && !request.tenantId().isBlank()
                && !user.getTenantId().equalsIgnoreCase(request.tenantId().trim())) {
            throw new UnauthorizedException("Tenant mismatch for the supplied credentials");
        }
        ensureUserIsActive(user);
    }

    private void ensureUserIsActive(UserAccount user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("User account is not active");
        }
    }

    private LoginResponse buildResponse(UserAccount user, String refreshToken) {
        Set<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(LinkedHashSet::new, Set::add, Set::addAll);

        String accessToken = jwtTokenGenerator.generateAccessToken(
                user.getId().toString(),
                user.getUsername(),
                user.getTenantId(),
                authorities
        );

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtProperties.getAccessTokenExpirationMinutes() * 60,
                refreshToken,
                userService.toResponse(user)
        );
    }

    private String issueRefreshToken(UserAccount user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshTokenExpirationDays(), ChronoUnit.DAYS));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken).getToken();
    }
}
