package com.hisabnikash.erp.masterdata.warehouse.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.warehouse.application.WarehouseService;
import com.hisabnikash.erp.masterdata.warehouse.dto.CreateWarehouseRequest;
import com.hisabnikash.erp.masterdata.warehouse.dto.UpdateWarehouseRequest;
import com.hisabnikash.erp.masterdata.warehouse.dto.WarehouseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:warehouse:create')")
    public ApiResponse<WarehouseResponse> create(@Valid @RequestBody CreateWarehouseRequest request) {
        return ApiResponse.success(service.create(request), "Warehouse created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:warehouse:view')")
    public ApiResponse<List<WarehouseResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:warehouse:view')")
    public ApiResponse<WarehouseResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.toResponse(service.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:warehouse:update')")
    public ApiResponse<WarehouseResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateWarehouseRequest request) {
        return ApiResponse.success(service.update(id, request), "Warehouse updated");
    }
}
