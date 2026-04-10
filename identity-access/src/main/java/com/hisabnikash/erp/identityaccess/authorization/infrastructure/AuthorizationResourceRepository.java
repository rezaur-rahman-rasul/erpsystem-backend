package com.hisabnikash.erp.identityaccess.authorization.infrastructure;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResource;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorizationResourceRepository extends JpaRepository<AuthorizationResource, UUID> {

    List<AuthorizationResource> findAllByOrderByFullCodeAsc();

    Optional<AuthorizationResource> findByFullCodeIgnoreCase(String fullCode);

    boolean existsByFullCodeIgnoreCase(String fullCode);

    List<AuthorizationResource> findByServiceCodeIgnoreCaseOrderByFullCodeAsc(String serviceCode);

    List<AuthorizationResource> findByTypeOrderByFullCodeAsc(AuthorizationResourceType type);
}
