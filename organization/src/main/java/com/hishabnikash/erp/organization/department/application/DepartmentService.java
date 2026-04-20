package com.hishabnikash.erp.organization.department.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.common.cache.OrganizationLookupCache;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.department.domain.Department;
import com.hishabnikash.erp.organization.department.dto.CreateDepartmentRequest;
import com.hishabnikash.erp.organization.department.dto.DepartmentResponse;
import com.hishabnikash.erp.organization.department.dto.UpdateDepartmentRequest;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import com.hishabnikash.erp.organization.department.mapper.DepartmentMapper;
import com.hishabnikash.erp.organization.department.validation.DepartmentHierarchyValidator;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentHierarchyValidator departmentHierarchyValidator;
    private final EventPublisher publisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final BranchRepository branchRepository;
    private final OrganizationLookupCache organizationLookupCache;

    @Auditable(action = "CREATE_DEPARTMENT")
    @CacheEvict(cacheNames = {CacheNames.DEPARTMENT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public DepartmentResponse create(CreateDepartmentRequest request) {

        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getBranchId(), request.getParentDepartmentId());
        departmentHierarchyValidator.validateNoCycle(null, request.getParentDepartmentId());

        Department saved = departmentRepository.save(departmentMapper.toEntity(request));

        publisher.publish(
                messagingProperties.getTopics().getDepartmentCreated(),
                "DepartmentCreated",
                "DEPARTMENT",
                saved.getId(),
                departmentMapper.toResponse(saved)
        );

        return departmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAll(UUID branchId, Pageable pageable) {
        Page<Department> departments = branchId == null
                ? departmentRepository.findAll(pageable)
                : departmentRepository.findByBranchId(branchId, pageable);
        return departments.map(departmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(UUID id) {
        return organizationLookupCache.findDepartmentResponseById(id)
                .getOrThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    @Auditable(action = "UPDATE_DEPARTMENT")
    @CacheEvict(cacheNames = {CacheNames.DEPARTMENT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
        Department department = findById(id);

        validateReferences(department.getLegalEntityId(), request.getBranchId(), request.getParentDepartmentId());
        departmentHierarchyValidator.validateNoCycle(id, request.getParentDepartmentId());
        departmentMapper.updateEntity(department, request);
        Department saved = departmentRepository.save(department);
        publisher.publish(
                messagingProperties.getTopics().getDepartmentUpdated(),
                "DepartmentUpdated",
                "DEPARTMENT",
                saved.getId(),
                departmentMapper.toResponse(saved)
        );
        return departmentMapper.toResponse(saved);
    }

    private void validateReferences(UUID legalEntityId, UUID branchId, UUID parentDepartmentId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (branchId != null && !branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found: " + branchId);
        }

        if (parentDepartmentId != null && !departmentRepository.existsById(parentDepartmentId)) {
            throw new ResourceNotFoundException("Parent department not found: " + parentDepartmentId);
        }
    }

    private Department findById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
