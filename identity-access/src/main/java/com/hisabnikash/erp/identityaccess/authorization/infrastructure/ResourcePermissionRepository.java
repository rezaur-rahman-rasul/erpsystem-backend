package com.hisabnikash.erp.identityaccess.authorization.infrastructure;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResource;
import com.hisabnikash.erp.identityaccess.authorization.domain.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {

    Optional<ResourcePermission> findByPermissionKeyIgnoreCase(String permissionKey);

    boolean existsByPermissionKeyIgnoreCase(String permissionKey);

    List<ResourcePermission> findAllByOrderByPermissionKeyAsc();

    List<ResourcePermission> findByResourceOrderByPermissionKeyAsc(AuthorizationResource resource);
}
