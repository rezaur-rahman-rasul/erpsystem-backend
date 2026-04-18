package com.hisabnikash.erp.masterdata.paymentterm.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.constants.CacheNames;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.paymentterm.domain.PaymentTerm;
import com.hisabnikash.erp.masterdata.paymentterm.dto.CreatePaymentTermRequest;
import com.hisabnikash.erp.masterdata.paymentterm.dto.PaymentTermResponse;
import com.hisabnikash.erp.masterdata.paymentterm.dto.UpdatePaymentTermRequest;
import com.hisabnikash.erp.masterdata.paymentterm.infrastructure.PaymentTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTermService {

    private final PaymentTermRepository paymentTermRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_PAYMENT_TERM")
    @CacheEvict(cacheNames = {CacheNames.PAYMENT_TERM_BY_ID, CacheNames.PAYMENT_TERM_LIST}, allEntries = true)
    public PaymentTermResponse create(CreatePaymentTermRequest request) {
        if (paymentTermRepository.existsByCodeIgnoreCase(request.code())) {
            throw new DuplicateResourceException("Payment term code already exists: " + request.code());
        }

        PaymentTerm paymentTerm = new PaymentTerm();
        apply(paymentTerm, request.code(), request.name(), request.dueDays(),
                request.discountDays(), request.discountPercentage(), request.active());
        PaymentTerm saved = paymentTermRepository.save(paymentTerm);
        PaymentTermResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getPaymentTermCreated(),
                "PaymentTermCreated",
                "PAYMENT_TERM",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PAYMENT_TERM_LIST, key = "'ALL'")
    public List<PaymentTermResponse> getAll() {
        return paymentTermRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PAYMENT_TERM_BY_ID, key = "#id")
    public PaymentTerm getById(UUID id) {
        return paymentTermRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment term not found: " + id));
    }

    @Auditable(action = "UPDATE_PAYMENT_TERM")
    @CacheEvict(cacheNames = {CacheNames.PAYMENT_TERM_BY_ID, CacheNames.PAYMENT_TERM_LIST}, allEntries = true)
    public PaymentTermResponse update(UUID id, UpdatePaymentTermRequest request) {
        PaymentTerm paymentTerm = getById(id);
        if (paymentTermRepository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw new DuplicateResourceException("Payment term code already exists: " + request.code());
        }

        apply(paymentTerm, request.code(), request.name(), request.dueDays(),
                request.discountDays(), request.discountPercentage(), request.active());
        PaymentTerm saved = paymentTermRepository.save(paymentTerm);
        PaymentTermResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getPaymentTermUpdated(),
                "PaymentTermUpdated",
                "PAYMENT_TERM",
                saved.getId(),
                response
        );
        return response;
    }

    public PaymentTermResponse toResponse(PaymentTerm paymentTerm) {
        return new PaymentTermResponse(
                paymentTerm.getId(),
                paymentTerm.getCode(),
                paymentTerm.getName(),
                paymentTerm.getDueDays(),
                paymentTerm.getDiscountDays(),
                paymentTerm.getDiscountPercentage(),
                paymentTerm.isActive(),
                paymentTerm.getCreatedBy(),
                paymentTerm.getCreatedAt(),
                paymentTerm.getUpdatedBy(),
                paymentTerm.getUpdatedAt()
        );
    }

    private void apply(PaymentTerm paymentTerm,
                       String code,
                       String name,
                       int dueDays,
                       Integer discountDays,
                       BigDecimal discountPercentage,
                       boolean active) {
        paymentTerm.setCode(code.trim().toUpperCase());
        paymentTerm.setName(name.trim());
        paymentTerm.setDueDays(dueDays);
        paymentTerm.setDiscountDays(discountDays);
        paymentTerm.setDiscountPercentage(discountPercentage == null ? null : discountPercentage.setScale(2, RoundingMode.HALF_UP));
        paymentTerm.setActive(active);
    }
}
