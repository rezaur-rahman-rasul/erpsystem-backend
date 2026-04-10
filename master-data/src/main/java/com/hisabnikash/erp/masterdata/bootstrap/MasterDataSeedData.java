package com.hisabnikash.erp.masterdata.bootstrap;

import com.hisabnikash.erp.masterdata.currency.domain.Currency;
import com.hisabnikash.erp.masterdata.currency.infrastructure.CurrencyRepository;
import com.hisabnikash.erp.masterdata.paymentterm.domain.PaymentTerm;
import com.hisabnikash.erp.masterdata.paymentterm.infrastructure.PaymentTermRepository;
import com.hisabnikash.erp.masterdata.taxcode.domain.TaxCode;
import com.hisabnikash.erp.masterdata.taxcode.infrastructure.TaxCodeRepository;
import com.hisabnikash.erp.masterdata.uom.domain.UnitOfMeasure;
import com.hisabnikash.erp.masterdata.uom.infrastructure.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MasterDataSeedData implements CommandLineRunner {

    private final CurrencyRepository currencyRepository;
    private final UnitOfMeasureRepository unitRepository;
    private final PaymentTermRepository paymentTermRepository;
    private final TaxCodeRepository taxCodeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (currencyRepository.count() == 0) {
            Currency usd = new Currency();
            usd.setCode("USD");
            usd.setName("US Dollar");
            usd.setSymbol("$");
            usd.setDecimalPlaces(2);
            usd.setActive(true);

            Currency bdt = new Currency();
            bdt.setCode("BDT");
            bdt.setName("Bangladeshi Taka");
            bdt.setSymbol("Tk");
            bdt.setDecimalPlaces(2);
            bdt.setActive(true);

            currencyRepository.save(usd);
            currencyRepository.save(bdt);
        }

        if (unitRepository.count() == 0) {
            UnitOfMeasure each = new UnitOfMeasure();
            each.setCode("EA");
            each.setName("Each");
            each.setCategory("COUNT");
            each.setBaseUnit(true);
            each.setConversionFactor(BigDecimal.ONE.setScale(6));
            each.setActive(true);

            UnitOfMeasure kilogram = new UnitOfMeasure();
            kilogram.setCode("KG");
            kilogram.setName("Kilogram");
            kilogram.setCategory("WEIGHT");
            kilogram.setBaseUnit(true);
            kilogram.setConversionFactor(BigDecimal.ONE.setScale(6));
            kilogram.setActive(true);

            unitRepository.save(each);
            unitRepository.save(kilogram);
        }

        if (paymentTermRepository.count() == 0) {
            PaymentTerm net30 = new PaymentTerm();
            net30.setCode("NET30");
            net30.setName("Net 30");
            net30.setDueDays(30);
            net30.setDiscountDays(null);
            net30.setDiscountPercentage(null);
            net30.setActive(true);
            paymentTermRepository.save(net30);
        }

        if (taxCodeRepository.count() == 0) {
            TaxCode vat15 = new TaxCode();
            vat15.setCode("VAT15");
            vat15.setName("VAT 15%");
            vat15.setRate(new BigDecimal("15.0000"));
            vat15.setInclusive(false);
            vat15.setActive(true);
            taxCodeRepository.save(vat15);
        }
    }
}
