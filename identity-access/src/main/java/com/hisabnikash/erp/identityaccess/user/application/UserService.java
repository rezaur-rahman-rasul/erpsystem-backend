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
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new DuplicateResourceException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }
        validateTenant(request.tenantId());

        UserAccount user = new UserAccount();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setDisplayName(request.displayName().trim());
        user.setTenantId(request.tenantId().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(resolveRoles(request.roleIds()));

        UserAccount saved = userRepository.save(user);
        UserResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getUserCreated(),
                "UserCreated",
                "USER",
                saved.getId(),
                response
        );
        return response;
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
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        String displayName = request.displayName().trim();
        String tenantId = request.tenantId().trim();

        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new DuplicateResourceException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }
        validateTenant(tenantId);

        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setTenantId(tenantId);
        user.setRoles(resolveRoles(request.roleIds()));

        UserAccount saved = userRepository.save(user);
        UserResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getUserUpdated(),
                "UserUpdated",
                "USER",
                saved.getId(),
                response
        );
        return response;
    }

    @Auditable(action = "CHANGE_USER_STATUS")
    @CacheEvict(cacheNames = {CacheNames.USER_BY_ID, CacheNames.USER_LIST}, allEntries = true)
    public UserResponse changeStatus(UUID id, UpdateUserStatusRequest request) {
        UserAccount user = getById(id);
        user.setStatus(request.status());
        UserAccount saved = userRepository.save(user);
        UserResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getUserStatusChanged(),
                "UserStatusChanged",
                "USER",
                saved.getId(),
                response
        );
        return response;
    }

    public UserResponse toResponse(UserAccount user) {
        Set<RoleSummaryResponse> roles = user.getRoles().stream()
                .map(role -> new RoleSummaryResponse(role.getId(), role.getCode(), role.getName()))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
        Set<OrganizationAccessResponse> organizationAccesses = organizationAccessRepository.findByUser_Id(user.getId()).stream()
                .map(access -> new OrganizationAccessResponse(
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
                .collect(LinkedHashSet::new, Set::add, Set::addAll);

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
                roles,
                organizationAccesses
        );
    }

    private void validateTenant(String tenantId) {
        if (tenantProfileReferenceRepository.count() == 0) {
            return;
        }
        if (!tenantProfileReferenceRepository.existsByTenantCodeIgnoreCaseAndActiveTrue(tenantId.trim())) {
            throw new InvalidRequestException("Tenant is not synchronized or inactive: " + tenantId);
        }
    }

    private Set<Role> resolveRoles(Set<UUID> roleIds) {
        return roleIds.stream()
                .map(roleService::getById)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }
}
