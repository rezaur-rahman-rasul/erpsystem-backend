package com.hisabnikash.erp.enterprisestructure.settings.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.branch.infrastructure.BranchRepository;
import com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure.BusinessUnitRepository;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.department.infrastructure.DepartmentRepository;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.location.infrastructure.LocationRepository;
import com.hisabnikash.erp.enterprisestructure.settings.domain.OrganizationSetting;
import com.hisabnikash.erp.enterprisestructure.settings.domain.SettingOwnerType;
import com.hisabnikash.erp.enterprisestructure.settings.dto.OrganizationSettingRequest;
import com.hisabnikash.erp.enterprisestructure.settings.dto.OrganizationSettingResponse;
import com.hisabnikash.erp.enterprisestructure.settings.infrastructure.OrganizationSettingRepository;
import com.hisabnikash.erp.enterprisestructure.settings.mapper.OrganizationSettingMapper;
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

    private final OrganizationSettingRepository repository;
    private final OrganizationSettingMapper mapper;
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

        if (repository.existsByOwnerTypeAndOwnerId(request.getOwnerType(), request.getOwnerId())) {
            throw new DuplicateResourceException("Settings already exist for owner");
        }

        OrganizationSetting saved = repository.save(mapper.toEntity(request));
        publishChangedEvent(saved);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = CacheNames.SETTINGS_BY_OWNER,
            key = "#ownerType.name() + ':' + #ownerId.toString()"
    )
    public OrganizationSettingResponse getByOwner(SettingOwnerType ownerType, UUID ownerId) {
        return mapper.toResponse(findByOwner(ownerType, ownerId));
    }

    @Auditable(action = "UPDATE_ORGANIZATION_SETTING")
    @CacheEvict(cacheNames = {CacheNames.SETTINGS_BY_OWNER, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public OrganizationSettingResponse update(SettingOwnerType ownerType,
                                              UUID ownerId,
                                              OrganizationSettingRequest request) {
        validateOwner(ownerType, ownerId);
        OrganizationSetting setting = findByOwner(ownerType, ownerId);
        mapper.updateEntity(setting, request);
        setting.setOwnerType(ownerType);
        setting.setOwnerId(ownerId);
        OrganizationSetting saved = repository.save(setting);
        publishChangedEvent(saved);
        return mapper.toResponse(saved);
    }

    private OrganizationSetting findByOwner(SettingOwnerType ownerType, UUID ownerId) {
        return repository.findByOwnerTypeAndOwnerId(ownerType, ownerId)
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
                mapper.toResponse(setting)
        );
    }
}
