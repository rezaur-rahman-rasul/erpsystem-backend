package com.hishabnikash.erp.organization.settings.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.location.infrastructure.LocationRepository;
import com.hishabnikash.erp.organization.settings.domain.OrganizationSetting;
import com.hishabnikash.erp.organization.settings.domain.SettingOwnerType;
import com.hishabnikash.erp.organization.settings.dto.OrganizationSettingRequest;
import com.hishabnikash.erp.organization.settings.dto.OrganizationSettingResponse;
import com.hishabnikash.erp.organization.settings.infrastructure.OrganizationSettingRepository;
import com.hishabnikash.erp.organization.settings.mapper.OrganizationSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationSettingService {

    private final OrganizationSettingRepository organizationSettingRepository;
    private final OrganizationSettingMapper organizationSettingMapper;
    private final LegalEntityRepository legalEntityRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_ORGANIZATION_SETTING")
    @CacheEvict(cacheNames = {CacheNames.SETTINGS_BY_OWNER, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public OrganizationSettingResponse create(OrganizationSettingRequest request) {
        validateOwner(request.getOwnerType(), request.getOwnerId());

        if (organizationSettingRepository.existsByOwnerTypeAndOwnerId(request.getOwnerType(), request.getOwnerId())) {
            throw new DuplicateResourceException("Settings already exist for owner");
        }

        OrganizationSetting saved = organizationSettingRepository.save(organizationSettingMapper.toEntity(request));
        publishChangedEvent(saved);
        return organizationSettingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = CacheNames.SETTINGS_BY_OWNER,
            key = "#ownerType.name() + ':' + #ownerId.toString()"
    )
    public OrganizationSettingResponse getByOwner(SettingOwnerType ownerType, UUID ownerId) {
        return organizationSettingMapper.toResponse(findByOwner(ownerType, ownerId));
    }

    @Auditable(action = "UPDATE_ORGANIZATION_SETTING")
    @CacheEvict(cacheNames = {CacheNames.SETTINGS_BY_OWNER, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public OrganizationSettingResponse update(SettingOwnerType ownerType,
                                              UUID ownerId,
                                              OrganizationSettingRequest request) {
        validateOwner(ownerType, ownerId);
        OrganizationSetting setting = findByOwner(ownerType, ownerId);
        organizationSettingMapper.updateEntity(setting, request);
        setting.setOwnerType(ownerType);
        setting.setOwnerId(ownerId);
        OrganizationSetting saved = organizationSettingRepository.save(setting);
        publishChangedEvent(saved);
        return organizationSettingMapper.toResponse(saved);
    }

    private OrganizationSetting findByOwner(SettingOwnerType ownerType, UUID ownerId) {
        return organizationSettingRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization settings not found for owner: " + ownerType + "/" + ownerId
                ));
    }

    private void validateOwner(SettingOwnerType ownerType, UUID ownerId) {
        boolean exists = switch (ownerType) {
            case LEGAL_ENTITY -> legalEntityRepository.existsById(ownerId);
            case BUSINESS_UNIT -> businessUnitRepository.existsById(ownerId);
            case BRANCH -> branchRepository.existsById(ownerId);
            case DEPARTMENT -> departmentRepository.existsById(ownerId);
            case LOCATION -> locationRepository.existsById(ownerId);
        };

        if (!exists) {
            throw new ResourceNotFoundException("Owner not found: " + ownerType + "/" + ownerId);
        }
    }

    private void publishChangedEvent(OrganizationSetting setting) {
        eventPublisher.publish(
                messagingProperties.getTopics().getOrganizationSettingChanged(),
                "OrganizationSettingChanged",
                "ORG_SETTING",
                setting.getId(),
                organizationSettingMapper.toResponse(setting)
        );
    }
}
