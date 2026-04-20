package com.hisabnikash.erp.gateway.catalog.api;

import com.hisabnikash.erp.gateway.catalog.dto.RouteDefinitionResponse;
import com.hisabnikash.erp.gateway.common.constants.CacheNames;
import com.hisabnikash.erp.gateway.common.response.ApiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteCatalogController {

    @GetMapping
    @Cacheable(cacheNames = CacheNames.ROUTE_CATALOG, key = "'ALL'", sync = true)
    public ApiResponse<List<RouteDefinitionResponse>> listRoutes() {
        return ApiResponse.success(List.of(
                new RouteDefinitionResponse("identity-access", "/identity/**", "identity-access", false, false, true, "v1"),
                new RouteDefinitionResponse("organization", "/organization/**", "organization", true, true, true, "v1"),
                new RouteDefinitionResponse("master-data", "/master-data/**", "master-data", true, true, true, "v1")
        ));
    }
}
