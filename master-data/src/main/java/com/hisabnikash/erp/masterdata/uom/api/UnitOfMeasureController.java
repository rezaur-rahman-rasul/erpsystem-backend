package com.hisabnikash.erp.masterdata.uom.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.uom.application.UnitOfMeasureService;
import com.hisabnikash.erp.masterdata.uom.dto.CreateUnitOfMeasureRequest;
import com.hisabnikash.erp.masterdata.uom.dto.UnitOfMeasureResponse;
import com.hisabnikash.erp.masterdata.uom.dto.UpdateUnitOfMeasureRequest;
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
@RequestMapping("/api/v1/units-of-measure")
@RequiredArgsConstructor
public class UnitOfMeasureController {

    private final UnitOfMeasureService unitOfMeasureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:uom:create')")
    public ApiResponse<UnitOfMeasureResponse> create(@Valid @RequestBody CreateUnitOfMeasureRequest request) {
        return ApiResponse.success(unitOfMeasureService.create(request), "Unit of measure created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:uom:view')")
    public ApiResponse<List<UnitOfMeasureResponse>> getAll() {
        return ApiResponse.success(unitOfMeasureService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:uom:view')")
    public ApiResponse<UnitOfMeasureResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(unitOfMeasureService.getResponseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:uom:update')")
    public ApiResponse<UnitOfMeasureResponse> update(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateUnitOfMeasureRequest request) {
        return ApiResponse.success(unitOfMeasureService.update(id, request), "Unit of measure updated");
    }
}
