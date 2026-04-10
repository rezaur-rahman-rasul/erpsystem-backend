package com.hisabnikash.erp.masterdata.customer.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.customer.domain.Customer;
import com.hisabnikash.erp.masterdata.customer.dto.CreateCustomerRequest;
import com.hisabnikash.erp.masterdata.customer.dto.CustomerResponse;
import com.hisabnikash.erp.masterdata.customer.dto.UpdateCustomerRequest;
import com.hisabnikash.erp.masterdata.customer.infrastructure.CustomerRepository;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository repository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_CUSTOMER")
    public CustomerResponse create(CreateCustomerRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, request.code())) {
            throw new DuplicateResourceException("Customer code already exists: " + request.code());
        }

        Customer customer = new Customer();
        apply(customer, tenantId, legalEntityId, request.code(), request.name(), request.email(), request.phone(), request.taxNumber(), request.active());
        Customer saved = repository.save(customer);
        CustomerResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCustomerCreated(),
                "CustomerCreated",
                "CUSTOMER",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return ownershipService.filterAccessible(repository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Customer getById(UUID id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return ownershipService.requireReadable(customer, "Customer");
    }

    @Auditable(action = "UPDATE_CUSTOMER")
    public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
        Customer customer = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(legalEntityId, request.code(), id)) {
            throw new DuplicateResourceException("Customer code already exists: " + request.code());
        }

        apply(customer, tenantId, legalEntityId, request.code(), request.name(), request.email(), request.phone(), request.taxNumber(), request.active());
        Customer saved = repository.save(customer);
        CustomerResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCustomerUpdated(),
                "CustomerUpdated",
                "CUSTOMER",
                saved.getId(),
                response
        );
        return response;
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getTenantId(),
                customer.getLegalEntityId(),
                customer.getCode(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getTaxNumber(),
                customer.isActive(),
                customer.getCreatedBy(),
                customer.getCreatedAt(),
                customer.getUpdatedBy(),
                customer.getUpdatedAt()
        );
    }

    private void apply(Customer customer,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       String email,
                       String phone,
                       String taxNumber,
                       boolean active) {
        customer.setTenantId(tenantId);
        customer.setLegalEntityId(legalEntityId);
        customer.setCode(code.trim().toUpperCase());
        customer.setName(name.trim());
        customer.setEmail(normalizeLower(email));
        customer.setPhone(normalize(phone));
        customer.setTaxNumber(normalize(taxNumber));
        customer.setActive(active);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeLower(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }
}
