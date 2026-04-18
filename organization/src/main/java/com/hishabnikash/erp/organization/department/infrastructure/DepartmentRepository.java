package com.hishabnikash.erp.organization.department.infrastructure;

import com.hishabnikash.erp.organization.department.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    boolean existsByCode(String code);

    List<Department> findByParentDepartmentId(UUID parentId);

    Page<Department> findByBranchId(UUID branchId, Pageable pageable);

    List<Department> findByBranchId(UUID branchId);

    List<Department> findByLegalEntityId(UUID legalEntityId);
}
