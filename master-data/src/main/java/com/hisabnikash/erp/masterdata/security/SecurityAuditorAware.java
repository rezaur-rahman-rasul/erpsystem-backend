package com.hisabnikash.erp.masterdata.security;

import com.hisabnikash.erp.masterdata.common.util.SecurityUtils;
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
