package com.hisabnikash.erp.enterprisestructure.config.properties;

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
        private String legalEntityCreated;
        private String legalEntityUpdated;
        private String businessUnitCreated;
        private String businessUnitUpdated;
        private String branchCreated;
        private String branchUpdated;
        private String departmentCreated;
        private String departmentUpdated;
        private String locationCreated;
        private String locationUpdated;
        private String costCenterCreated;
        private String costCenterUpdated;
        private String profitCenterCreated;
        private String profitCenterUpdated;
        private String subsidiaryCreated;
        private String subsidiaryUpdated;
        private String fiscalCalendarCreated;
        private String fiscalCalendarUpdated;
        private String tenantProfileCreated;
        private String tenantProfileUpdated;
        private String organizationSettingChanged;
    }
}
