package com.hisabnikash.erp.enterprisestructure.branch.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.branch.domain.Branch;
import com.hisabnikash.erp.enterprisestructure.branch.domain.BranchStatus;
import com.hisabnikash.erp.enterprisestructure.branch.dto.BranchResponse;
import com.hisabnikash.erp.enterprisestructure.branch.dto.CreateBranchRequest;
import com.hisabnikash.erp.enterprisestructure.branch.dto.UpdateBranchRequest;
import com.hisabnikash.erp.enterprisestructure.branch.infrastructure.BranchRepository;
import com.hisabnikash.erp.enterprisestructure.branch.mapper.BranchMapper;
import com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure.BusinessUnitRepository;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.InvalidRequestException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hisabnikash.erp.enterprisestructure.integration.masterdata.infrastructure.WarehouseReferenceRepository;
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

    private final BranchRepository repository;
    private final BranchMapper mapper;
    private final EventPublisher publisher;
    private final MessagingProperties messagingProperties;
    private final BusinessUnitRepository businessUnitRepository;
    private final com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository legalEntityRepository;
    private final OrganizationAccessReferenceRepository organizationAccessReferenceRepository;
    private final WarehouseReferenceRepository warehouseReferenceRepository;

    @Auditable(action = "CREATE_BRANCH")
    @CacheEvict(cacheNames = {CacheNames.BRANCH_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BranchResponse create(CreateBranchRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Branch code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getBusinessUnitId());

        Branch branch = mapper.toEntity(request);
        Branch saved = repository.save(branch);

        publisher.publish(
                messagingProperties.getTopics().getBranchCreated(),
                "BranchCreated",
                "BRANCH",
                saved.getId(),
                mapper.toResponse(saved)
        );

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BranchResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<Branch> branches = legalEntityId == null
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);
        return branches
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.BRANCH_BY_ID, key = "#id")
    public BranchResponse getById(UUID id) {
        Branch b = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
        return mapper.toResponse(b);
    }

    @Auditable(action = "UPDATE_BRANCH")
    @CacheEvict(cacheNames = {CacheNames.BRANCH_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BranchResponse update(UUID id, UpdateBranchRequest req) {
        Branch b = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));

        if (b.getStatus() != BranchStatus.INACTIVE && req.getStatus() == BranchStatus.INACTIVE) {
            validateBranchCanBeInactivated(id);
        }
        validateReferences(b.getLegalEntityId(), req.getBusinessUnitId());
        mapper.updateEntity(b, req);
        Branch saved = repository.save(b);

        publisher.publish(
                messagingProperties.getTopics().getBranchUpdated(),
                "BranchUpdated",
                "BRANCH",
                saved.getId(),
                mapper.toResponse(saved)
        );

        return mapper.toResponse(saved);
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
}
