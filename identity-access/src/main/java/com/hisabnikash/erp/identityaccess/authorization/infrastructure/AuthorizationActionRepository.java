package com.hisabnikash.erp.identityaccess.authorization.infrastructure;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationActionRepository extends JpaRepository<AuthorizationAction, String> {
}
