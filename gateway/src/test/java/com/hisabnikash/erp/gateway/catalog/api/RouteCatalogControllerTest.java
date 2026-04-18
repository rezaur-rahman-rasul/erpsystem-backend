package com.hisabnikash.erp.gateway.catalog.api;

import com.hisabnikash.erp.gateway.catalog.dto.RouteDefinitionResponse;
import com.hisabnikash.erp.gateway.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RouteCatalogControllerTest {

    private final RouteCatalogController controller = new RouteCatalogController();

    @Test
    void listRoutesReturnsSuccessfulResponse() {
        ApiResponse<List<RouteDefinitionResponse>> response = controller.listRoutes();

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isNull();
        assertThat(response.data()).hasSize(3);
    }

    @ParameterizedTest
    @CsvSource({
            "identity-access,/identity/**,identity-access,false,false,true,v1",
            "organization,/organization/**,organization,true,true,true,v1",
            "master-data,/master-data/**,master-data,true,true,true,v1"
    })
    void listRoutesExposesExpectedRouteDefinitions(
            String id,
            String externalPath,
            String targetService,
            boolean authenticated,
            boolean tenantAware,
            boolean throttled,
            String apiVersion
    ) {
        List<RouteDefinitionResponse> routes = controller.listRoutes().data();

        RouteDefinitionResponse route = routes.stream()
                .filter(currentRoute -> currentRoute.id().equals(id))
                .findFirst()
                .orElseThrow();

        assertThat(route.externalPath()).isEqualTo(externalPath);
        assertThat(route.targetService()).isEqualTo(targetService);
        assertThat(route.authenticated()).isEqualTo(authenticated);
        assertThat(route.tenantAware()).isEqualTo(tenantAware);
        assertThat(route.throttled()).isEqualTo(throttled);
        assertThat(route.apiVersion()).isEqualTo(apiVersion);
    }
}
