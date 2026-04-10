package com.hisabnikash.erp.identityaccess.security;

import com.hisabnikash.erp.identityaccess.common.util.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("securityAuditorAware")
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserIdOrSystem());
    }
}
