package com.hisabnikash.erp.masterdata.product.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.product.domain.Product;
import com.hisabnikash.erp.masterdata.product.dto.CreateProductRequest;
import com.hisabnikash.erp.masterdata.product.dto.ProductResponse;
import com.hisabnikash.erp.masterdata.product.dto.UpdateProductRequest;
import com.hisabnikash.erp.masterdata.product.infrastructure.ProductRepository;
import com.hisabnikash.erp.masterdata.uom.infrastructure.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_PRODUCT")
    public ProductResponse create(CreateProductRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, request.code())) {
            throw new DuplicateResourceException("Product code already exists: " + request.code());
        }

        validateUnitOfMeasure(request.unitOfMeasureId());

        Product product = new Product();
        apply(product, tenantId, legalEntityId, request.code(), request.name(), request.description(), request.unitOfMeasureId(), request.active());
        Product saved = repository.save(product);
        ProductResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getProductCreated(),
                "ProductCreated",
                "PRODUCT",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return ownershipService.filterAccessible(repository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return ownershipService.requireReadable(product, "Product");
    }

    @Auditable(action = "UPDATE_PRODUCT")
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(legalEntityId, request.code(), id)) {
            throw new DuplicateResourceException("Product code already exists: " + request.code());
        }

        validateUnitOfMeasure(request.unitOfMeasureId());
        apply(product, tenantId, legalEntityId, request.code(), request.name(), request.description(), request.unitOfMeasureId(), request.active());
        Product saved = repository.save(product);
        ProductResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getProductUpdated(),
                "ProductUpdated",
                "PRODUCT",
                saved.getId(),
                response
        );
        return response;
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTenantId(),
                product.getLegalEntityId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getUnitOfMeasureId(),
                product.isActive(),
                product.getCreatedBy(),
                product.getCreatedAt(),
                product.getUpdatedBy(),
                product.getUpdatedAt()
        );
    }

    private void validateUnitOfMeasure(UUID unitOfMeasureId) {
        if (!unitOfMeasureRepository.existsById(unitOfMeasureId)) {
            throw new ResourceNotFoundException("Unit of measure not found: " + unitOfMeasureId);
        }
    }

    private void apply(Product product,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       String description,
                       UUID unitOfMeasureId,
                       boolean active) {
        product.setTenantId(tenantId);
        product.setLegalEntityId(legalEntityId);
        product.setCode(code.trim().toUpperCase());
        product.setName(name.trim());
        product.setDescription(normalize(description));
        product.setUnitOfMeasureId(unitOfMeasureId);
        product.setActive(active);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
