package com.hishabnikash.erp.organization.branch.api;

import com.hishabnikash.erp.organization.branch.application.BranchService;
import com.hishabnikash.erp.organization.branch.dto.BranchResponse;
import com.hishabnikash.erp.organization.branch.dto.CreateBranchRequest;
import com.hishabnikash.erp.organization.branch.dto.UpdateBranchRequest;
import com.hishabnikash.erp.organization.common.response.ApiResponse;
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
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:branch:create')")
    public ApiResponse<BranchResponse> create(@Valid @RequestBody CreateBranchRequest request) {
        return ApiResponse.success(branchService.create(request), "Branch created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:branch:view')")
    public ApiResponse<Page<BranchResponse>> getAll(@RequestParam(required = false) UUID legalEntityId,
                                                    Pageable pageable) {
        return ApiResponse.success(branchService.getAll(legalEntityId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:branch:view')")
    public ApiResponse<BranchResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(branchService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:branch:update')")
    public ApiResponse<BranchResponse> update(@PathVariable UUID id,
                                              @Valid @RequestBody UpdateBranchRequest request) {
        return ApiResponse.success(branchService.update(id, request), "Branch updated");
    }
}
