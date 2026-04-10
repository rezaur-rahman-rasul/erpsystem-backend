package com.hisabnikash.erp.identityaccess.security.principal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CurrentUser implements AuthenticatedPrincipal {

    private final String userId;
    private final String username;
    private final String tenantId;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getName() {
        return username != null ? username : userId;
    }
}
