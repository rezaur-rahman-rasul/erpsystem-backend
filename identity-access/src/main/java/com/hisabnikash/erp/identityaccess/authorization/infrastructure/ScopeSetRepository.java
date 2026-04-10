package com.hisabnikash.erp.identityaccess.authorization.infrastructure;

import com.hisabnikash.erp.identityaccess.authorization.domain.ScopeSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScopeSetRepository extends JpaRepository<ScopeSet, UUID> {

    Optional<ScopeSet> findByScopeHash(String scopeHash);
}
