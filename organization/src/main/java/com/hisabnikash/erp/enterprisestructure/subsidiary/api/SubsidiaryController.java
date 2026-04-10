package com.hisabnikash.erp.enterprisestructure.subsidiary.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.subsidiary.application.SubsidiaryService;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.CreateSubsidiaryRequest;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.SubsidiaryResponse;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.UpdateSubsidiaryRequest;
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
@RequestMapping("/api/v1/subsidiaries")
@RequiredArgsConstructor
public class SubsidiaryController {

    private final SubsidiaryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:subsidiary:create')")
    public ApiResponse<SubsidiaryResponse> create(@Valid @RequestBody CreateSubsidiaryRequest request) {
        return ApiResponse.success(service.create(request), "Subsidiary created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:subsidiary:view')")
    public ApiResponse<Page<SubsidiaryResponse>> getAll(@RequestParam(required = false) UUID parentLegalEntityId,
                                                        Pageable pageable) {
        return ApiResponse.success(service.getAll(parentLegalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:subsidiary:view')")
    public ApiResponse<SubsidiaryResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:subsidiary:update')")
    public ApiResponse<SubsidiaryResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateSubsidiaryRequest request) {
        return ApiResponse.success(service.update(id, request), "Subsidiary updated");
    }
}
