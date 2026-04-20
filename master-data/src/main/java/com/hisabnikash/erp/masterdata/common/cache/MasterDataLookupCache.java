package com.hisabnikash.erp.masterdata.common.cache;

import com.hisabnikash.erp.cachesupport.CachedLookupResult;
import com.hisabnikash.erp.masterdata.common.constants.CacheNames;
import com.hisabnikash.erp.masterdata.currency.domain.Currency;
import com.hisabnikash.erp.masterdata.currency.dto.CurrencyResponse;
import com.hisabnikash.erp.masterdata.currency.infrastructure.CurrencyRepository;
import com.hisabnikash.erp.masterdata.paymentterm.domain.PaymentTerm;
import com.hisabnikash.erp.masterdata.paymentterm.dto.PaymentTermResponse;
import com.hisabnikash.erp.masterdata.paymentterm.infrastructure.PaymentTermRepository;
import com.hisabnikash.erp.masterdata.uom.domain.UnitOfMeasure;
import com.hisabnikash.erp.masterdata.uom.dto.UnitOfMeasureResponse;
import com.hisabnikash.erp.masterdata.uom.infrastructure.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MasterDataLookupCache {

    private final CurrencyRepository currencyRepository;
    private final PaymentTermRepository paymentTermRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    @Cacheable(cacheNames = CacheNames.CURRENCY_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<CurrencyResponse> findCurrencyResponseById(UUID id) {
        return currencyRepository.findById(id)
                .map(this::toCurrencyResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.PAYMENT_TERM_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<PaymentTermResponse> findPaymentTermResponseById(UUID id) {
        return paymentTermRepository.findById(id)
                .map(this::toPaymentTermResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.UNIT_OF_MEASURE_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<UnitOfMeasureResponse> findUnitOfMeasureResponseById(UUID id) {
        return unitOfMeasureRepository.findById(id)
                .map(this::toUnitOfMeasureResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    private CurrencyResponse toCurrencyResponse(Currency currency) {
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

    private PaymentTermResponse toPaymentTermResponse(PaymentTerm paymentTerm) {
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

    private UnitOfMeasureResponse toUnitOfMeasureResponse(UnitOfMeasure unit) {
        return new UnitOfMeasureResponse(
                unit.getId(),
                unit.getCode(),
                unit.getName(),
                unit.getCategory(),
                unit.isBaseUnit(),
                unit.getConversionFactor(),
                unit.isActive(),
                unit.getCreatedBy(),
                unit.getCreatedAt(),
                unit.getUpdatedBy(),
                unit.getUpdatedAt()
        );
    }
}
