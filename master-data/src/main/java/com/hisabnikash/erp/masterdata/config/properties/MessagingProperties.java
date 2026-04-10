package com.hisabnikash.erp.masterdata.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {

    private String provider;
    private final Topics topics = new Topics();

    @Getter
    @Setter
    public static class Topics {
        private String customerCreated;
        private String customerUpdated;
        private String supplierCreated;
        private String supplierUpdated;
        private String warehouseCreated;
        private String warehouseUpdated;
        private String taxCodeCreated;
        private String taxCodeUpdated;
        private String productCreated;
        private String productUpdated;
        private String employeeCreated;
        private String employeeUpdated;
        private String chartOfAccountCreated;
        private String chartOfAccountUpdated;
        private String currencyCreated;
        private String currencyUpdated;
        private String unitOfMeasureCreated;
        private String unitOfMeasureUpdated;
        private String paymentTermCreated;
        private String paymentTermUpdated;
    }
}
