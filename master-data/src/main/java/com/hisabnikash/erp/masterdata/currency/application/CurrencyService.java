package com.hisabnikash.erp.masterdata.currency.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.constants.CacheNames;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.currency.domain.Currency;
import com.hisabnikash.erp.masterdata.currency.dto.CreateCurrencyRequest;
import com.hisabnikash.erp.masterdata.currency.dto.CurrencyResponse;
import com.hisabnikash.erp.masterdata.currency.dto.UpdateCurrencyRequest;
import com.hisabnikash.erp.masterdata.currency.infrastructure.CurrencyRepository;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_CURRENCY")
    @CacheEvict(cacheNames = {CacheNames.CURRENCY_BY_ID, CacheNames.CURRENCY_LIST}, allEntries = true)
    public CurrencyResponse create(CreateCurrencyRequest request) {
        if (currencyRepository.existsByCodeIgnoreCase(request.code())) {
            throw new DuplicateResourceException("Currency code already exists: " + request.code());
        }

        Currency currency = new Currency();
        apply(currency, request.code(), request.name(), request.symbol(), request.decimalPlaces(), request.active());
        Currency saved = currencyRepository.save(currency);
        CurrencyResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCurrencyCreated(),
                "CurrencyCreated",
                "CURRENCY",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CURRENCY_LIST, key = "'ALL'")
    public List<CurrencyResponse> getAll() {
        return currencyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CURRENCY_BY_ID, key = "#id")
    public Currency getById(UUID id) {
        return currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + id));
    }

    @Auditable(action = "UPDATE_CURRENCY")
    @CacheEvict(cacheNames = {CacheNames.CURRENCY_BY_ID, CacheNames.CURRENCY_LIST}, allEntries = true)
    public CurrencyResponse update(UUID id, UpdateCurrencyRequest request) {
        Currency currency = getById(id);
        if (currencyRepository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) {
            throw new DuplicateResourceException("Currency code already exists: " + request.code());
        }

        apply(currency, request.code(), request.name(), request.symbol(), request.decimalPlaces(), request.active());
        Currency saved = currencyRepository.save(currency);
        CurrencyResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCurrencyUpdated(),
                "CurrencyUpdated",
                "CURRENCY",
                saved.getId(),
                response
        );
        return response;
    }

    public CurrencyResponse toResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getName(),
                currency.getSymbol(),
                currency.getDecimalPlaces(),
                currency.isActive(),
                currency.getCreatedBy(),
                currency.getCreatedAt(),
                currency.getUpdatedBy(),
                currency.getUpdatedAt()
        );
    }

    private void apply(Currency currency,
                       String code,
                       String name,
                       String symbol,
                       int decimalPlaces,
                       boolean active) {
        currency.setCode(code.trim().toUpperCase());
        currency.setName(name.trim());
        currency.setSymbol(symbol.trim());
        currency.setDecimalPlaces(decimalPlaces);
        currency.setActive(active);
    }
}
