package com.hisabnikash.erp.identityaccess.organizationaccess.application;

import com.hisabnikash.erp.identityaccess.audit.aop.Auditable;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.identityaccess.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.identityaccess.config.properties.MessagingProperties;
import com.hisabnikash.erp.identityaccess.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.identityaccess.integration.organization.domain.BranchReference;
import com.hisabnikash.erp.identityaccess.integration.organization.domain.LegalEntityReference;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.LegalEntityReferenceRepository;
import com.hisabnikash.erp.identityaccess.organizationaccess.domain.OrganizationAccessAssignment;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.CreateOrganizationAccessRequest;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.UpdateOrganizationAccessRequest;
import com.hisabnikash.erp.identityaccess.organizationaccess.infrastructure.OrganizationAccessAssignmentRepository;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationAccessService {

    private final OrganizationAccessAssignmentRepository organizationAccessAssignmentRepository;
    private final UserAccountRepository userRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityReferenceRepository legalEntityReferenceRepository;
    private final BranchReferenceRepository branchReferenceRepository;

    @Auditable(action = "CREATE_ORGANIZATION_ACCESS")
    @CacheEvict(
            cacheNames = {
                    CacheNames.ORGANIZATION_ACCESS_BY_USER,
                    CacheNames.USER_BY_ID,
                    CacheNames.USER_LIST
            },
            allEntries = true
    )
    public OrganizationAccessResponse create(UUID userId, CreateOrganizationAccessRequest request) {
        UserAccount user = resolveUser(userId);
        if (organizationAccessAssignmentRepository.existsByUser_IdAndLegalEntityIdAndBranchId(userId, request.legalEntityId(), request.branchId())) {
            throw new DuplicateResourceException("Organization access already exists for user: " + userId);
        }

        validateAccessScope(request.legalEntityId(), request.branchId());

        if (request.primaryAccess()) {
            clearPrimaryAccess(userId);
        }

        OrganizationAccessAssignment assignment = new OrganizationAccessAssignment();
        assignment.setUser(user);
        assignment.setLegalEntityId(request.legalEntityId());
        assignment.setBranchId(request.branchId());
        assignment.setPrimaryAccess(request.primaryAccess());

        OrganizationAccessAssignment saved = organizationAccessAssignmentRepository.save(assignment);
        OrganizationAccessResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getOrganizationAccessCreated(),
                "OrganizationAccessCreated",
                "ORGANIZATION_ACCESS",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ORGANIZATION_ACCESS_BY_USER, key = "#userId")
    public Set<OrganizationAccessResponse> getAllByUserId(UUID userId) {
        resolveUser(userId);
        return organizationAccessAssignmentRepository.findByUser_Id(userId).stream()
                .map(this::toResponse)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    @Auditable(action = "UPDATE_ORGANIZATION_ACCESS")
    @CacheEvict(
            cacheNames = {
                    CacheNames.ORGANIZATION_ACCESS_BY_USER,
                    CacheNames.USER_BY_ID,
                    CacheNames.USER_LIST
            },
            allEntries = true
    )
    public OrganizationAccessResponse update(UUID userId, UUID accessId, UpdateOrganizationAccessRequest request) {
        resolveUser(userId);
        OrganizationAccessAssignment assignment = organizationAccessAssignmentRepository.findByIdAndUser_Id(accessId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization access not found: " + accessId));

        if (request.primaryAccess()) {
            clearPrimaryAccess(userId);
        }

        assignment.setPrimaryAccess(request.primaryAccess());
        OrganizationAccessAssignment saved = organizationAccessAssignmentRepository.save(assignment);
        OrganizationAccessResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getOrganizationAccessUpdated(),
                "OrganizationAccessUpdated",
                "ORGANIZATION_ACCESS",
                saved.getId(),
                response
        );
        return response;
    }

    public OrganizationAccessResponse toResponse(OrganizationAccessAssignment assignment) {
        return new OrganizationAccessResponse(
                assignment.getId(),
                assignment.getUser().getId(),
                assignment.getLegalEntityId(),
                assignment.getBranchId(),
                assignment.isPrimaryAccess(),
                assignment.getCreatedBy(),
                assignment.getCreatedAt(),
                assignment.getUpdatedBy(),
                assignment.getUpdatedAt()
        );
    }

    private void validateAccessScope(UUID legalEntityId, UUID branchId) {
        LegalEntityReference legalEntity = legalEntityReferenceRepository.findById(legalEntityId)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity reference not available yet: " + legalEntityId));
        if (!legalEntity.isActive()) {
            throw new ResourceNotFoundException("Legal entity is not active: " + legalEntityId);
        }

        if (branchId == null) {
            return;
        }

        BranchReference branch = branchReferenceRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch reference not available yet: " + branchId));
        if (!branch.isActive()) {
            throw new ResourceNotFoundException("Branch is not active: " + branchId);
        }
        if (!branch.getLegalEntityId().equals(legalEntityId)) {
            throw new IllegalArgumentException("Branch does not belong to the supplied legal entity");
        }
    }

    private UserAccount resolveUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private void clearPrimaryAccess(UUID userId) {
        List<OrganizationAccessAssignment> assignments = organizationAccessAssignmentRepository.findByUser_Id(userId);
        assignments.forEach(assignment -> assignment.setPrimaryAccess(false));
    }
}
