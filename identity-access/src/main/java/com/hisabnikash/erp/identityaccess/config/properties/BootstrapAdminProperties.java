package com.hisabnikash.erp.identityaccess.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bootstrap.admin")
public class BootstrapAdminProperties {

    private String username;
    private String email;
    private String password;
    private String tenantId;
    private String displayName;
}
