package com.hisabnikash.erp.masterdata.supplier.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.supplier.application.SupplierService;
import com.hisabnikash.erp.masterdata.supplier.dto.CreateSupplierRequest;
import com.hisabnikash.erp.masterdata.supplier.dto.SupplierResponse;
import com.hisabnikash.erp.masterdata.supplier.dto.UpdateSupplierRequest;
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
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:supplier:create')")
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        return ApiResponse.success(service.create(request), "Supplier created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:supplier:view')")
    public ApiResponse<List<SupplierResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:supplier:view')")
    public ApiResponse<SupplierResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.toResponse(service.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:supplier:update')")
    public ApiResponse<SupplierResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateSupplierRequest request) {
        return ApiResponse.success(service.update(id, request), "Supplier updated");
    }
}
