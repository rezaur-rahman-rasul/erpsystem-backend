package com.hisabnikash.erp.masterdata.employee.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.employee.domain.Employee;
import com.hisabnikash.erp.masterdata.employee.dto.CreateEmployeeRequest;
import com.hisabnikash.erp.masterdata.employee.dto.EmployeeResponse;
import com.hisabnikash.erp.masterdata.employee.dto.UpdateEmployeeRequest;
import com.hisabnikash.erp.masterdata.employee.infrastructure.EmployeeRepository;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_EMPLOYEE")
    public EmployeeResponse create(CreateEmployeeRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (employeeRepository.existsByLegalEntityIdAndEmployeeNumberIgnoreCase(legalEntityId, request.employeeNumber())) {
            throw new DuplicateResourceException("Employee number already exists: " + request.employeeNumber());
        }

        Employee employee = new Employee();
        apply(employee, tenantId, legalEntityId, request.employeeNumber(), request.fullName(), request.email(), request.phone(), request.designation(), request.active());
        Employee saved = employeeRepository.save(employee);
        EmployeeResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getEmployeeCreated(),
                "EmployeeCreated",
                "EMPLOYEE",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return ownershipService.filterAccessible(employeeRepository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Employee getById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return ownershipService.requireReadable(employee, "Employee");
    }

    @Auditable(action = "UPDATE_EMPLOYEE")
    public EmployeeResponse update(UUID id, UpdateEmployeeRequest request) {
        Employee employee = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (employeeRepository.existsByLegalEntityIdAndEmployeeNumberIgnoreCaseAndIdNot(legalEntityId, request.employeeNumber(), id)) {
            throw new DuplicateResourceException("Employee number already exists: " + request.employeeNumber());
        }

        apply(employee, tenantId, legalEntityId, request.employeeNumber(), request.fullName(), request.email(), request.phone(), request.designation(), request.active());
        Employee saved = employeeRepository.save(employee);
        EmployeeResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getEmployeeUpdated(),
                "EmployeeUpdated",
                "EMPLOYEE",
                saved.getId(),
                response
        );
        return response;
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getTenantId(),
                employee.getLegalEntityId(),
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDesignation(),
                employee.isActive(),
                employee.getCreatedBy(),
                employee.getCreatedAt(),
                employee.getUpdatedBy(),
                employee.getUpdatedAt()
        );
    }

    private void apply(Employee employee,
                       String tenantId,
                       UUID legalEntityId,
                       String employeeNumber,
                       String fullName,
                       String email,
                       String phone,
                       String designation,
                       boolean active) {
        employee.setTenantId(tenantId);
        employee.setLegalEntityId(legalEntityId);
        employee.setEmployeeNumber(employeeNumber.trim().toUpperCase());
        employee.setFullName(fullName.trim());
        employee.setEmail(normalizeLower(email));
        employee.setPhone(normalize(phone));
        employee.setDesignation(normalize(designation));
        employee.setActive(active);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeLower(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }
}
