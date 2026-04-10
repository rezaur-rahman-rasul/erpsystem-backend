package com.hisabnikash.erp.masterdata.paymentterm.api;

import com.hisabnikash.erp.masterdata.common.response.ApiResponse;
import com.hisabnikash.erp.masterdata.paymentterm.application.PaymentTermService;
import com.hisabnikash.erp.masterdata.paymentterm.dto.CreatePaymentTermRequest;
import com.hisabnikash.erp.masterdata.paymentterm.dto.PaymentTermResponse;
import com.hisabnikash.erp.masterdata.paymentterm.dto.UpdatePaymentTermRequest;
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
@RequestMapping("/api/v1/payment-terms")
@RequiredArgsConstructor
public class PaymentTermController {

    private final PaymentTermService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('master-data:payment-term:create')")
    public ApiResponse<PaymentTermResponse> create(@Valid @RequestBody CreatePaymentTermRequest request) {
        return ApiResponse.success(service.create(request), "Payment term created");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('master-data:payment-term:view')")
    public ApiResponse<List<PaymentTermResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:payment-term:view')")
    public ApiResponse<PaymentTermResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(service.toResponse(service.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('master-data:payment-term:update')")
    public ApiResponse<PaymentTermResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdatePaymentTermRequest request) {
        return ApiResponse.success(service.update(id, request), "Payment term updated");
    }
}
