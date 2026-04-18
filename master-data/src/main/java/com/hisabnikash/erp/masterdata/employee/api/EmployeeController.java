package com.hisabnikash.erp.masterdata.employee.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.employee.application.EmployeeService;
import com.hisabnikash.erp.masterdata.employee.dto.CreateEmployeeRequest;
import com.hisabnikash.erp.masterdata.employee.dto.EmployeeResponse;
import com.hisabnikash.erp.masterdata.employee.dto.UpdateEmployeeRequest;
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
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:employee:create')")
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        return ApiResponse.success(employeeService.create(request), "Employee created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:employee:view')")
    public ApiResponse<List<EmployeeResponse>> getAll() {
        return ApiResponse.success(employeeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:employee:view')")
    public ApiResponse<EmployeeResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(employeeService.toResponse(employeeService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:employee:update')")
    public ApiResponse<EmployeeResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateEmployeeRequest request) {
        return ApiResponse.success(employeeService.update(id, request), "Employee updated");
    }
}
