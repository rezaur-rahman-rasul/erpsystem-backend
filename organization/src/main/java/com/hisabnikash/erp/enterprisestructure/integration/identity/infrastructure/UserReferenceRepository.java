package com.hisabnikash.erp.enterprisestructure.integration.identity.infrastructure;

import com.hisabnikash.erp.enterprisestructure.integration.identity.domain.UserReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserReferenceRepository extends JpaRepository<UserReference, UUID> {
}
