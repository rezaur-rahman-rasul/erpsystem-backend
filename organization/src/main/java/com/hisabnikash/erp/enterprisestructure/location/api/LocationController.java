package com.hisabnikash.erp.enterprisestructure.location.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.location.application.LocationService;
import com.hisabnikash.erp.enterprisestructure.location.dto.CreateLocationRequest;
import com.hisabnikash.erp.enterprisestructure.location.dto.LocationResponse;
import com.hisabnikash.erp.enterprisestructure.location.dto.UpdateLocationRequest;
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

    private final LocationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:location:create')")
    public ApiResponse<LocationResponse> create(@Valid @RequestBody CreateLocationRequest request) {
        return ApiResponse.success(service.create(request), "Location created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:location:view')")
    public ApiResponse<Page<LocationResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                      @RequestParam(required = false) UUID branchId,
                                                      Pageable pageable) {
        return ApiResponse.success(service.getAll(legalEntityId, branchId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:location:view')")
    public ApiResponse<LocationResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:location:update')")
    public ApiResponse<LocationResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateLocationRequest request) {
        return ApiResponse.success(service.update(id, request), "Location updated");
    }
}
