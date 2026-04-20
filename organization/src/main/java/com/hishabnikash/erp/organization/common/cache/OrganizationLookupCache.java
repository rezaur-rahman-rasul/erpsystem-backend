package com.hishabnikash.erp.organization.common.cache;

import com.hisabnikash.erp.cachesupport.CachedLookupResult;
import com.hishabnikash.erp.organization.branch.dto.BranchResponse;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.branch.mapper.BranchMapper;
import com.hishabnikash.erp.organization.businessunit.dto.BusinessUnitResponse;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.businessunit.mapper.BusinessUnitMapper;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.department.dto.DepartmentResponse;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import com.hishabnikash.erp.organization.department.mapper.DepartmentMapper;
import com.hishabnikash.erp.organization.legalentity.dto.LegalEntityResponse;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.legalentity.mapper.LegalEntityMapper;
import com.hishabnikash.erp.organization.location.dto.LocationResponse;
import com.hishabnikash.erp.organization.location.infrastructure.LocationRepository;
import com.hishabnikash.erp.organization.location.mapper.LocationMapper;
import com.hishabnikash.erp.organization.settings.domain.SettingOwnerType;
import com.hishabnikash.erp.organization.settings.dto.OrganizationSettingResponse;
import com.hishabnikash.erp.organization.settings.infrastructure.OrganizationSettingRepository;
import com.hishabnikash.erp.organization.settings.mapper.OrganizationSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationLookupCache {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final BusinessUnitRepository businessUnitRepository;
    private final BusinessUnitMapper businessUnitMapper;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityMapper legalEntityMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final OrganizationSettingRepository organizationSettingRepository;
    private final OrganizationSettingMapper organizationSettingMapper;

    @Cacheable(cacheNames = CacheNames.BRANCH_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<BranchResponse> findBranchResponseById(UUID id) {
        return branchRepository.findById(id)
                .map(branchMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.BUSINESS_UNIT_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<BusinessUnitResponse> findBusinessUnitResponseById(UUID id) {
        return businessUnitRepository.findById(id)
                .map(businessUnitMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.DEPARTMENT_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<DepartmentResponse> findDepartmentResponseById(UUID id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.LEGAL_ENTITY_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<LegalEntityResponse> findLegalEntityResponseById(UUID id) {
        return legalEntityRepository.findById(id)
                .map(legalEntityMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.LOCATION_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<LocationResponse> findLocationResponseById(UUID id) {
        return locationRepository.findById(id)
                .map(locationMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(
            cacheNames = CacheNames.SETTINGS_BY_OWNER,
            key = "#ownerType.name() + ':' + #ownerId.toString()",
            sync = true
    )
    public CachedLookupResult<OrganizationSettingResponse> findSettingsByOwner(
            SettingOwnerType ownerType,
            UUID ownerId
    ) {
        return organizationSettingRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId)
                .map(organizationSettingMapper::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }
}
