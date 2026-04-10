package com.hisabnikash.erp.identityaccess.organizationaccess.infrastructure;

import com.hisabnikash.erp.identityaccess.organizationaccess.domain.OrganizationAccessAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationAccessAssignmentRepository extends JpaRepository<OrganizationAccessAssignment, UUID> {

    List<OrganizationAccessAssignment> findByUser_Id(UUID userId);

    Optional<OrganizationAccessAssignment> findByIdAndUser_Id(UUID id, UUID userId);

    boolean existsByUser_IdAndLegalEntityIdAndBranchId(UUID userId, UUID legalEntityId, UUID branchId);
}
