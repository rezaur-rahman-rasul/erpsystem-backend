package com.hishabnikash.erp.organization.department.api;

import com.hishabnikash.erp.organization.common.response.ApiResponse;
import com.hishabnikash.erp.organization.department.application.DepartmentService;
import com.hishabnikash.erp.organization.department.dto.CreateDepartmentRequest;
import com.hishabnikash.erp.organization.department.dto.DepartmentResponse;
import com.hishabnikash.erp.organization.department.dto.UpdateDepartmentRequest;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('enterprise:department:create')")
    public ApiResponse<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest request) {
        return ApiResponse.success(departmentService.create(request), "Department created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('enterprise:department:view')")
    public ApiResponse<Page<DepartmentResponse>> getAll(@RequestParam(required = false) UUID branchId,
                                                        Pageable pageable) {
        return ApiResponse.success(departmentService.getAll(branchId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:department:view')")
    public ApiResponse<DepartmentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(departmentService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('enterprise:department:update')")
    public ApiResponse<DepartmentResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateDepartmentRequest request) {
        return ApiResponse.success(departmentService.update(id, request), "Department updated");
    }
}
