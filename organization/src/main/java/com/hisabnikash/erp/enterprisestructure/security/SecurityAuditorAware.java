package com.hisabnikash.erp.enterprisestructure.security;

import com.hisabnikash.erp.enterprisestructure.security.principal.CurrentUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser user)) {
            return Optional.of("system");
        }

        return Optional.of(user.getUserId());
    }
}
