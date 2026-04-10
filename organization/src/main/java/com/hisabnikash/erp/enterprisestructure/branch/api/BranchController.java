package com.hisabnikash.erp.enterprisestructure.branch.api;

import com.hisabnikash.erp.enterprisestructure.branch.application.BranchService;
import com.hisabnikash.erp.enterprisestructure.branch.dto.BranchResponse;
import com.hisabnikash.erp.enterprisestructure.branch.dto.CreateBranchRequest;
import com.hisabnikash.erp.enterprisestructure.branch.dto.UpdateBranchRequest;
import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:branch:create')")
    public ApiResponse<BranchResponse> create(@Valid @RequestBody CreateBranchRequest request) {
        return ApiResponse.success(service.create(request), "Branch created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:branch:view')")
    public ApiResponse<Page<BranchResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                    Pageable pageable) {
        return ApiResponse.success(service.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:branch:view')")
    public ApiResponse<BranchResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:branch:update')")
    public ApiResponse<BranchResponse> update(@PathVariable UUID id,
                                              @Valid @RequestBody UpdateBranchRequest req) {
        return ApiResponse.success(service.update(id, req), "Branch updated");
    }
}
