package com.hishabnikash.erp.organization.legalentity.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.legalentity.application.LegalEntityService;
import com.hishabnikash.erp.organization.legalentity.dto.ChangeLegalEntityStatusRequest;
import com.hishabnikash.erp.organization.legalentity.dto.CreateLegalEntityRequest;
import com.hishabnikash.erp.organization.legalentity.dto.LegalEntityResponse;
import com.hishabnikash.erp.organization.legalentity.dto.UpdateLegalEntityRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/legal-entities")
@RequiredArgsConstructor
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:legal-entity:create')")
    public ApiResponse<LegalEntityResponse> create(@Valid @RequestBody CreateLegalEntityRequest request) {
        return ApiResponse.success(legalEntityService.create(request), "Legal entity created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:legal-entity:view')")
    public ApiResponse<Page<LegalEntityResponse>> getAll(Pageable pageable) {
        return ApiResponse.success(legalEntityService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:view')")
    public ApiResponse<LegalEntityResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(legalEntityService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:update')")
    public ApiResponse<LegalEntityResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdateLegalEntityRequest request) {
        return ApiResponse.success(legalEntityService.update(id, request), "Legal entity updated");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('enterprise:legal-entity:update')")
    public ApiResponse<LegalEntityResponse> changeStatus(@PathVariable UUID id,
                                                         @Valid @RequestBody ChangeLegalEntityStatusRequest request) {
        return ApiResponse.success(legalEntityService.changeStatus(id, request), "Status updated");
    }
}
