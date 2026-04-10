package com.hisabnikash.erp.enterprisestructure.location.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.branch.infrastructure.BranchRepository;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.location.domain.Location;
import com.hisabnikash.erp.enterprisestructure.location.dto.CreateLocationRequest;
import com.hisabnikash.erp.enterprisestructure.location.dto.LocationResponse;
import com.hisabnikash.erp.enterprisestructure.location.dto.UpdateLocationRequest;
import com.hisabnikash.erp.enterprisestructure.location.infrastructure.LocationRepository;
import com.hisabnikash.erp.enterprisestructure.location.mapper.LocationMapper;
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

    private final LocationRepository repository;
    private final LocationMapper mapper;
    private final LegalEntityRepository legalEntityRepository;
    private final BranchRepository branchRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_LOCATION")
    @CacheEvict(cacheNames = {CacheNames.LOCATION_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LocationResponse create(CreateLocationRequest request) {
        validateCreate(request);
        Location saved = repository.save(mapper.toEntity(request));
        eventPublisher.publish(
                messagingProperties.getTopics().getLocationCreated(),
                "LocationCreated",
                "LOCATION",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> getAll(UUID legalEntityId, UUID branchId, Pageable pageable) {
        if (branchId != null) {
            return repository.findByBranchId(branchId, pageable).map(mapper::toResponse);
        }

        if (legalEntityId != null) {
            return repository.findByLegalEntityId(legalEntityId, pageable).map(mapper::toResponse);
        }

        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.LOCATION_BY_ID, key = "#id")
    public LocationResponse getById(UUID id) {
        return mapper.toResponse(findById(id));
    }

    @Auditable(action = "UPDATE_LOCATION")
    @CacheEvict(cacheNames = {CacheNames.LOCATION_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LocationResponse update(UUID id, UpdateLocationRequest request) {
        Location location = findById(id);
        validateReferences(location.getLegalEntityId(), request.getBranchId());
        mapper.updateEntity(location, request);
        Location saved = repository.save(location);
        eventPublisher.publish(
                messagingProperties.getTopics().getLocationUpdated(),
                "LocationUpdated",
                "LOCATION",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    private void validateCreate(CreateLocationRequest request) {
        if (repository.existsByCode(request.getCode())) {
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
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + id));
    }
}
