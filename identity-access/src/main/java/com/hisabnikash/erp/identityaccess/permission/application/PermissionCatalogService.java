package com.hisabnikash.erp.identityaccess.permission.application;

import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.permission.dto.PermissionDefinitionResponse;
import com.hisabnikash.erp.identityaccess.security.permission.PhaseOnePermissions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionCatalogService {

    @Cacheable(cacheNames = CacheNames.PERMISSION_CATALOG, key = "'ALL'", sync = true)
    public List<PermissionDefinitionResponse> getAll() {
        return PhaseOnePermissions.catalog();
    }
}
