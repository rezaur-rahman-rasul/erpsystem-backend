package com.hisabnikash.erp.enterprisestructure.department.api;

import com.hisabnikash.erp.enterprisestructure.common.response.ApiResponse;
import com.hisabnikash.erp.enterprisestructure.department.application.DepartmentService;
import com.hisabnikash.erp.enterprisestructure.department.dto.CreateDepartmentRequest;
import com.hisabnikash.erp.enterprisestructure.department.dto.DepartmentResponse;
import com.hisabnikash.erp.enterprisestructure.department.dto.UpdateDepartmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:department:create')")
    public ApiResponse<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest req) {
        return ApiResponse.success(service.create(req), "Department created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:department:view')")
    public ApiResponse<Page<DepartmentResponse>> getAll(@RequestParam(required = false) UUID branchId,
                                                        Pageable pageable) {
        return ApiResponse.success(service.getAll(branchId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:department:view')")
    public ApiResponse<DepartmentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:department:update')")
    public ApiResponse<DepartmentResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateDepartmentRequest req) {
        return ApiResponse.success(service.update(id, req), "Department updated");
    }
}
