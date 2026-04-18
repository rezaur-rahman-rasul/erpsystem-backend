package com.hishabnikash.erp.organization.branch.infrastructure;

import com.hishabnikash.erp.organization.branch.domain.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    boolean existsByCode(String code);

    Page<Branch> findByLegalEntityId(UUID legalEntityId, Pageable pageable);

    List<Branch> findByLegalEntityId(UUID legalEntityId);
}
