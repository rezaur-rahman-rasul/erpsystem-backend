package com.hisabnikash.erp.identityaccess.authorization.seed;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResourceType;

import java.util.List;

public record PermissionSeedDefinition(
        String legacyCode,
        String serviceCode,
        String serviceName,
        String moduleCode,
        String moduleName,
        String resourcePath,
        AuthorizationResourceType resourceType,
        String resourceName,
        String actionCode,
        String description
) {

    public String fullResourceCode() {
        return serviceCode + "." + moduleCode + "." + resourcePath;
    }

    public String permissionKey() {
        return fullResourceCode() + "#" + actionCode;
    }

    public boolean hasApiGroup() {
        return resourcePath.startsWith("API.");
    }

    public String apiGroupCode() {
        return serviceCode + "." + moduleCode + ".API";
    }

    public String resourceCode() {
        List<String> segments = List.of(resourcePath.split("\\."));
        return segments.get(segments.size() - 1);
    }
}
