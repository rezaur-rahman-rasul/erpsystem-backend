package com.hisabnikash.erp.gateway.catalog.dto;

public record RouteDefinitionResponse(
        String id,
        String externalPath,
        String targetService,
        boolean authenticated,
        boolean tenantAware,
        boolean throttled,
        String apiVersion
) {
}
