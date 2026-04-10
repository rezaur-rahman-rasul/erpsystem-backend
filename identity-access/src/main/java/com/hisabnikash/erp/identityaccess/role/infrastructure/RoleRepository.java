package com.hisabnikash.erp.identityaccess.role.infrastructure;

import com.hisabnikash.erp.identityaccess.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    Optional<Role> findByCodeIgnoreCase(String code);
}
