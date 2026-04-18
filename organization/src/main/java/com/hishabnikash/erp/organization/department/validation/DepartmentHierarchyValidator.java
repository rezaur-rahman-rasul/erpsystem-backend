package com.hishabnikash.erp.organization.department.validation;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DepartmentHierarchyValidator {

    private final DepartmentRepository departmentRepository;

    public void validateNoCycle(UUID currentId, UUID newParentId) {

        if (newParentId == null) {
            return;
        }

        UUID parent = newParentId;

        while (parent != null) {
            if (parent.equals(currentId)) {
                throw new IllegalArgumentException("Circular department hierarchy detected");
            }

            parent = departmentRepository.findById(parent)
                    .map(d -> d.getParentDepartmentId())
                    .orElse(null);
        }
    }
}
