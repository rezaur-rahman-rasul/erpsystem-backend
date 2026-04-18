package com.hishabnikash.erp.organization.location.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.location.domain.Location;
import com.hishabnikash.erp.organization.location.dto.CreateLocationRequest;
import com.hishabnikash.erp.organization.location.dto.LocationResponse;
import com.hishabnikash.erp.organization.location.dto.UpdateLocationRequest;
import com.hishabnikash.erp.organization.location.infrastructure.LocationRepository;
import com.hishabnikash.erp.organization.location.mapper.LocationMapper;
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
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LegalEntityRepository legalEntityRepository;
    private final BranchRepository branchRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_LOCATION")
    @CacheEvict(cacheNames = {CacheNames.LOCATION_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LocationResponse create(CreateLocationRequest request) {
        validateCreate(request);
        Location saved = locationRepository.save(locationMapper.toEntity(request));
        eventPublisher.publish(
                messagingProperties.getTopics().getLocationCreated(),
                "LocationCreated",
                "LOCATION",
                saved.getId(),
                locationMapper.toResponse(saved)
        );
        return locationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> getAll(UUID legalEntityId, UUID branchId, Pageable pageable) {
        if (branchId != null) {
            return locationRepository.findByBranchId(branchId, pageable).map(locationMapper::toResponse);
        }

        if (legalEntityId != null) {
            return locationRepository.findByLegalEntityId(legalEntityId, pageable).map(locationMapper::toResponse);
        }

        return locationRepository.findAll(pageable).map(locationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.LOCATION_BY_ID, key = "#id")
    public LocationResponse getById(UUID id) {
        return locationMapper.toResponse(findById(id));
    }

    @Auditable(action = "UPDATE_LOCATION")
    @CacheEvict(cacheNames = {CacheNames.LOCATION_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LocationResponse update(UUID id, UpdateLocationRequest request) {
        Location location = findById(id);
        validateReferences(location.getLegalEntityId(), request.getBranchId());
        locationMapper.updateEntity(location, request);
        Location saved = locationRepository.save(location);
        eventPublisher.publish(
                messagingProperties.getTopics().getLocationUpdated(),
                "LocationUpdated",
                "LOCATION",
                saved.getId(),
                locationMapper.toResponse(saved)
        );
        return locationMapper.toResponse(saved);
    }

    private void validateCreate(CreateLocationRequest request) {
        if (locationRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Location code already exists: " + request.getCode());
        }
        validateReferences(request.getLegalEntityId(), request.getBranchId());
    }

    private void validateReferences(UUID legalEntityId, UUID branchId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (branchId != null && !branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found: " + branchId);
        }
    }

    private Location findById(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + id));
    }
}
