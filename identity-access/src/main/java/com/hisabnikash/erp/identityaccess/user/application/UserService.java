package com.hisabnikash.erp.identityaccess.user.application;

import com.hisabnikash.erp.identityaccess.audit.aop.Auditable;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.identityaccess.common.exception.InvalidRequestException;
import com.hisabnikash.erp.identityaccess.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.identityaccess.config.properties.MessagingProperties;
import com.hisabnikash.erp.identityaccess.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.TenantProfileReferenceRepository;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.organizationaccess.infrastructure.OrganizationAccessAssignmentRepository;
import com.hisabnikash.erp.identityaccess.role.application.RoleService;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import com.hisabnikash.erp.identityaccess.user.dto.CreateUserRequest;
import com.hisabnikash.erp.identityaccess.user.dto.RoleSummaryResponse;
import com.hisabnikash.erp.identityaccess.user.dto.UpdateUserRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UpdateUserStatusRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserAccountRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final OrganizationAccessAssignmentRepository organizationAccessRepository;
    private final TenantProfileReferenceRepository tenantProfileReferenceRepository;

    @Auditable(action = "CREATE_USER")
    @CacheEvict(cacheNames = {CacheNames.USER_BY_ID, CacheNames.USER_LIST}, allEntries = true)
    public UserResponse create(CreateUserRequest request) {
        String username = normalizeText(request.username());
        String email = normalizeEmail(request.email());
        String displayName = normalizeText(request.displayName());
        String tenantId = normalizeText(request.tenantId());

        ensureUsernameAvailable(username, null);
        ensureEmailAvailable(email, null);
        validateTenant(tenantId);

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setTenantId(tenantId);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(resolveRoles(request.roleIds()));

        return saveAndPublish(
                user,
                messagingProperties.getTopics().getUserCreated(),
                "UserCreated"
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.USER_LIST, key = "'ALL'")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAccount getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.USER_BY_ID, key = "#id")
    public UserResponse getResponseById(UUID id) {
        return toResponse(getById(id));
    }

    @Auditable(action = "UPDATE_USER")
    @CacheEvict(cacheNames = {CacheNames.USER_BY_ID, CacheNames.USER_LIST}, allEntries = true)
    public UserResponse update(UUID id, UpdateUserRequest request) {
        UserAccount user = getById(id);
        String username = normalizeText(request.username());
        String email = normalizeEmail(request.email());
        String displayName = normalizeText(request.displayName());
        String tenantId = normalizeText(request.tenantId());

        ensureUsernameAvailable(username, id);
        ensureEmailAvailable(email, id);
        validateTenant(tenantId);

        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setTenantId(tenantId);
        user.setRoles(resolveRoles(request.roleIds()));

        return saveAndPublish(
                user,
                messagingProperties.getTopics().getUserUpdated(),
                "UserUpdated"
        );
    }

    @Auditable(action = "CHANGE_USER_STATUS")
    @CacheEvict(cacheNames = {CacheNames.USER_BY_ID, CacheNames.USER_LIST}, allEntries = true)
    public UserResponse changeStatus(UUID id, UpdateUserStatusRequest request) {
        UserAccount user = getById(id);
        user.setStatus(request.status());

        return saveAndPublish(
                user,
                messagingProperties.getTopics().getUserStatusChanged(),
                "UserStatusChanged"
        );
    }

    public UserResponse toResponse(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getTenantId(),
                user.getStatus(),
                user.getCreatedBy(),
                user.getCreatedAt(),
                user.getUpdatedBy(),
                user.getUpdatedAt(),
                buildRoleSummaries(user),
                buildOrganizationAccessResponses(user)
        );
    }

    private UserResponse saveAndPublish(UserAccount user, String topic, String eventType) {
        UserAccount saved = userRepository.save(user);
        UserResponse response = toResponse(saved);

        eventPublisher.publish(
                topic,
                eventType,
                "USER",
                saved.getId(),
                response
        );

        return response;
    }

    private Set<RoleSummaryResponse> buildRoleSummaries(UserAccount user) {
        Set<RoleSummaryResponse> roles = new LinkedHashSet<>();

        for (Role role : user.getRoles()) {
            roles.add(new RoleSummaryResponse(role.getId(), role.getCode(), role.getName()));
        }

        return roles;
    }

    private Set<OrganizationAccessResponse> buildOrganizationAccessResponses(UserAccount user) {
        Set<OrganizationAccessResponse> organizationAccesses = new LinkedHashSet<>();

        organizationAccessRepository.findByUser_Id(user.getId()).forEach(access ->
                organizationAccesses.add(new OrganizationAccessResponse(
                        access.getId(),
                        user.getId(),
                        access.getLegalEntityId(),
                        access.getBranchId(),
                        access.isPrimaryAccess(),
                        access.getCreatedBy(),
                        access.getCreatedAt(),
                        access.getUpdatedBy(),
                        access.getUpdatedAt()
                ))
        );

        return organizationAccesses;
    }

    private void ensureUsernameAvailable(String username, UUID currentUserId) {
        boolean exists = currentUserId == null
                ? userRepository.existsByUsernameIgnoreCase(username)
                : userRepository.existsByUsernameIgnoreCaseAndIdNot(username, currentUserId);

        if (exists) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }
    }

    private void ensureEmailAvailable(String email, UUID currentUserId) {
        boolean exists = currentUserId == null
                ? userRepository.existsByEmailIgnoreCase(email)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(email, currentUserId);

        if (exists) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }
    }

    private void validateTenant(String tenantId) {
        if (tenantProfileReferenceRepository.count() == 0) {
            return;
        }

        if (!tenantProfileReferenceRepository.existsByTenantCodeIgnoreCaseAndActiveTrue(tenantId)) {
            throw new InvalidRequestException("Tenant is not synchronized or inactive: " + tenantId);
        }
    }

    private Set<Role> resolveRoles(Set<UUID> roleIds) {
        Set<Role> roles = new LinkedHashSet<>();

        for (UUID roleId : roleIds) {
            roles.add(roleService.getById(roleId));
        }

        return roles;
    }

    private String normalizeText(String value) {
        return value.trim();
    }

    private String normalizeEmail(String value) {
        return normalizeText(value).toLowerCase();
    }
}
