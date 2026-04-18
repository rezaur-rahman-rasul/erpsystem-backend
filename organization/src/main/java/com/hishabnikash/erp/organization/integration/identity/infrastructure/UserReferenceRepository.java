package com.hishabnikash.erp.organization.integration.identity.infrastructure;

import com.hishabnikash.erp.organization.integration.identity.domain.UserReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserReferenceRepository extends JpaRepository<UserReference, UUID> {
}
