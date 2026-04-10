package com.hisabnikash.erp.masterdata.uom.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.constants.CacheNames;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.uom.domain.UnitOfMeasure;
import com.hisabnikash.erp.masterdata.uom.dto.CreateUnitOfMeasureRequest;
import com.hisabnikash.erp.masterdata.uom.dto.UnitOfMeasureResponse;
import com.hisabnikash.erp.masterdata.uom.dto.UpdateUnitOfMeasureRequest;
import com.hisabnikash.erp.masterdata.uom.infrastructure.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository repository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_UNIT_OF_MEASURE")
    @CacheEvict(cacheNames = {CacheNames.UNIT_OF_MEASURE_BY_ID, CacheNames.UNIT_OF_MEASURE_LIST}, allEntries = true)
    public UnitOfMeasureResponse create(CreateUnitOfMeasureRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw new DuplicateResourceException("Unit of measure code already exists: " + request.code());
        }

        UnitOfMeasure unit = new UnitOfMeasure();
        apply(unit, request.code(), request.name(), request.category(), request.baseUnit(),
                request.conversionFactor().setScale(6, RoundingMode.HALF_UP), request.active());
        UnitOfMeasure saved = repository.save(unit);
        UnitOfMeasureResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getUnitOfMeasureCreated(),
                "UnitOfMeasureCreated",
                "UNIT_OF_MEASURE",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.UNIT_OF_MEASURE_LIST, key = "'ALL'")
    public List<UnitOfMeasureResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.UNIT_OF_MEASURE_BY_ID, key = "#id")
    public UnitOfMeasure getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found: " + id));
    }

    @Auditable(action = "UPDATE_UNIT_OF_MEASURE")
    @CacheEvict(cacheNames = {CacheNames.UNIT_OF_MEASURE_BY_ID, CacheNames.UNIT_OF_MEASURE_LIST}, allEntries = true)
    public UnitOfMeasureResponse update(UUID id, UpdateUnitOfMeasureRequest request) {
        UnitOfMeasure unit = getById(id);
        if (repository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw new DuplicateResourceException("Unit of measure code already exists: " + request.code());
        }

        apply(unit, request.code(), request.name(), request.category(), request.baseUnit(),
                request.conversionFactor().setScale(6, RoundingMode.HALF_UP), request.active());
        UnitOfMeasure saved = repository.save(unit);
        UnitOfMeasureResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getUnitOfMeasureUpdated(),
                "UnitOfMeasureUpdated",
                "UNIT_OF_MEASURE",
                saved.getId(),
                response
        );
        return response;
    }

    public UnitOfMeasureResponse toResponse(UnitOfMeasure unit) {
        return new UnitOfMeasureResponse(
                unit.getId(),
                unit.getCode(),
                unit.getName(),
                unit.getCategory(),
                unit.isBaseUnit(),
                unit.getConversionFactor(),
                unit.isActive(),
                unit.getCreatedBy(),
                unit.getCreatedAt(),
                unit.getUpdatedBy(),
                unit.getUpdatedAt()
        );
    }

    private void apply(UnitOfMeasure unit,
                       String code,
                       String name,
                       String category,
                       boolean baseUnit,
                       java.math.BigDecimal conversionFactor,
                       boolean active) {
        unit.setCode(code.trim().toUpperCase());
        unit.setName(name.trim());
        unit.setCategory(category.trim());
        unit.setBaseUnit(baseUnit);
        unit.setConversionFactor(conversionFactor);
        unit.setActive(active);
    }
}
