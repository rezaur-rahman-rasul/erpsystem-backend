package com.hishabnikash.erp.organization.branch.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.branch.domain.Branch;
import com.hishabnikash.erp.organization.branch.domain.BranchStatus;
import com.hishabnikash.erp.organization.branch.dto.BranchResponse;
import com.hishabnikash.erp.organization.branch.dto.CreateBranchRequest;
import com.hishabnikash.erp.organization.branch.dto.UpdateBranchRequest;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.branch.mapper.BranchMapper;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.common.cache.OrganizationLookupCache;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.InvalidRequestException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hishabnikash.erp.organization.integration.masterdata.infrastructure.WarehouseReferenceRepository;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final EventPublisher publisher;
    private final MessagingProperties messagingProperties;
    private final BusinessUnitRepository businessUnitRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final OrganizationAccessReferenceRepository organizationAccessReferenceRepository;
    private final WarehouseReferenceRepository warehouseReferenceRepository;
    private final OrganizationLookupCache organizationLookupCache;

    @Auditable(action = "CREATE_BRANCH")
    @CacheEvict(cacheNames = {CacheNames.BRANCH_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BranchResponse create(CreateBranchRequest request) {
        if (branchRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Branch code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getBusinessUnitId());

        Branch branch = branchMapper.toEntity(request);
        return saveAndPublish(
                branch,
                messagingProperties.getTopics().getBranchCreated(),
                "BranchCreated"
        );
    }

    @Transactional(readOnly = true)
    public Page<BranchResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<Branch> branches = legalEntityId == null
                ? branchRepository.findAll(pageable)
                : branchRepository.findByLegalEntityId(legalEntityId, pageable);
        return branches.map(branchMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BranchResponse getById(UUID id) {
        return organizationLookupCache.findBranchResponseById(id)
                .getOrThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
    }

    @Auditable(action = "UPDATE_BRANCH")
    @CacheEvict(cacheNames = {CacheNames.BRANCH_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BranchResponse update(UUID id, UpdateBranchRequest request) {
        Branch branch = findBranch(id);

        if (branch.getStatus() != BranchStatus.INACTIVE && request.getStatus() == BranchStatus.INACTIVE) {
            validateBranchCanBeInactivated(id);
        }

        validateReferences(branch.getLegalEntityId(), request.getBusinessUnitId());
        branchMapper.updateEntity(branch, request);

        return saveAndPublish(
                branch,
                messagingProperties.getTopics().getBranchUpdated(),
                "BranchUpdated"
        );
    }

    private void validateReferences(UUID legalEntityId, UUID businessUnitId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (businessUnitId != null && !businessUnitRepository.existsById(businessUnitId)) {
            throw new ResourceNotFoundException("Business unit not found: " + businessUnitId);
        }
    }

    private void validateBranchCanBeInactivated(UUID branchId) {
        if (warehouseReferenceRepository.existsByBranchIdAndActiveTrue(branchId)) {
            throw new InvalidRequestException("Branch cannot be inactivated while active warehouses exist");
        }

        if (organizationAccessReferenceRepository.existsByBranchId(branchId)) {
            throw new InvalidRequestException("Branch cannot be inactivated while access assignments reference it");
        }
    }

    private Branch findBranch(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
    }

    private BranchResponse saveAndPublish(Branch branch, String topic, String eventType) {
        Branch saved = branchRepository.save(branch);
        BranchResponse response = branchMapper.toResponse(saved);

        publisher.publish(
                topic,
                eventType,
                "BRANCH",
                saved.getId(),
                response
        );

        return response;
    }
}
