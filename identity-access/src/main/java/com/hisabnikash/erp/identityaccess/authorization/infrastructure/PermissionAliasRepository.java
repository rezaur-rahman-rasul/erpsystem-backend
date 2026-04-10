package com.hisabnikash.erp.identityaccess.authorization.infrastructure;

import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAliasType;
import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionAliasRepository extends JpaRepository<PermissionAlias, UUID> {

    Optional<PermissionAlias> findByAliasCodeIgnoreCase(String aliasCode);

    List<PermissionAlias> findByPermission_Id(UUID permissionId);

    Optional<PermissionAlias> findFirstByPermission_IdAndAliasTypeOrderByAliasCodeAsc(UUID permissionId,
                                                                                       PermissionAliasType aliasType);
}
