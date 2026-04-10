package com.hisabnikash.erp.enterprisestructure.department.validation;
import com.hisabnikash.erp.enterprisestructure.department.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DepartmentHierarchyValidator {

    private final DepartmentRepository repository;

    public void validateNoCycle(UUID currentId, UUID newParentId) {

        if (newParentId == null) {
            return;
        }

        UUID parent = newParentId;

        while (parent != null) {
            if (parent.equals(currentId)) {
                throw new IllegalArgumentException("Circular department hierarchy detected");
            }

            parent = repository.findById(parent)
                    .map(d -> d.getParentDepartmentId())
                    .orElse(null);
        }
    }
}
