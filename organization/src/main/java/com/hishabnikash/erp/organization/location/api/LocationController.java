package com.hishabnikash.erp.organization.location.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.location.application.LocationService;
import com.hishabnikash.erp.organization.location.dto.CreateLocationRequest;
import com.hishabnikash.erp.organization.location.dto.LocationResponse;
import com.hishabnikash.erp.organization.location.dto.UpdateLocationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:location:create')")
    public ApiResponse<LocationResponse> create(@Valid @RequestBody CreateLocationRequest request) {
        return ApiResponse.success(locationService.create(request), "Location created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:location:view')")
    public ApiResponse<Page<LocationResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                      @RequestParam(required = false) UUID branchId,
                                                      Pageable pageable) {
        return ApiResponse.success(locationService.getAll(legalEntityId, branchId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:location:view')")
    public ApiResponse<LocationResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(locationService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:location:update')")
    public ApiResponse<LocationResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateLocationRequest request) {
        return ApiResponse.success(locationService.update(id, request), "Location updated");
    }
}
