package com.hisabnikash.erp.identityaccess.config.properties;

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
        private String userCreated;
        private String userUpdated;
        private String userStatusChanged;
        private String roleCreated;
        private String roleUpdated;
        private String organizationAccessCreated;
        private String organizationAccessUpdated;
    }
}
