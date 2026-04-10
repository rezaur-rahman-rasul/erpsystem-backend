package com.hisabnikash.erp.enterprisestructure.legalentity.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.legalentity.application.LegalEntityService;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.ChangeLegalEntityStatusRequest;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.CreateLegalEntityRequest;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.LegalEntityResponse;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.UpdateLegalEntityRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/legal-entities")
@RequiredArgsConstructor
public class LegalEntityController {

    private final LegalEntityService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:legal-entity:create')")
    public ApiResponse<LegalEntityResponse> create(@Valid @RequestBody CreateLegalEntityRequest request) {
        return ApiResponse.success(service.create(request), "Legal entity created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:legal-entity:view')")
    public ApiResponse<Page<LegalEntityResponse>> getAll(Pageable pageable) {
        return ApiResponse.success(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:view')")
    public ApiResponse<LegalEntityResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:update')")
    public ApiResponse<LegalEntityResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdateLegalEntityRequest request) {
        return ApiResponse.success(service.update(id, request), "Legal entity updated");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:update')")
    public ApiResponse<LegalEntityResponse> changeStatus(@PathVariable UUID id,
                                                         @Valid @RequestBody ChangeLegalEntityStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "Status updated");
    }
}
