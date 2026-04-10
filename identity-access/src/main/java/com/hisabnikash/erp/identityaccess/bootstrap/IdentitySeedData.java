package com.hisabnikash.erp.identityaccess.bootstrap;

import com.hisabnikash.erp.identityaccess.config.properties.BootstrapAdminProperties;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.role.application.RoleService;
import com.hisabnikash.erp.identityaccess.role.infrastructure.RoleRepository;
import com.hisabnikash.erp.identityaccess.security.permission.PhaseOnePermissions;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(10)
public class IdentitySeedData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties adminProperties;
    private final RoleService roleService;

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByCodeIgnoreCase("PLATFORM_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setCode("PLATFORM_ADMIN");
                    role.setName("Platform Administrator");
                    role.setDescription("Full access across Phase 1 services");
                    roleService.applyPermissions(role, PhaseOnePermissions.all());
                    return roleRepository.save(role);
                });
        syncAdminRole(adminRole);

        UserAccount admin = userRepository.findByUsernameIgnoreCase(adminProperties.getUsername())
                .orElseGet(() -> createAdmin(adminRole));
        ensureAdminHasPlatformAccess(admin, adminRole);
    }

    private void syncAdminRole(Role adminRole) {
        adminRole.setName("Platform Administrator");
        adminRole.setDescription("Full access across Phase 1 services");
        roleService.applyPermissions(adminRole, PhaseOnePermissions.all());
        roleRepository.save(adminRole);
    }

    private UserAccount createAdmin(Role adminRole) {
        UserAccount admin = new UserAccount();
        admin.setUsername(adminProperties.getUsername());
        admin.setEmail(adminProperties.getEmail());
        admin.setDisplayName(adminProperties.getDisplayName());
        admin.setTenantId(adminProperties.getTenantId());
        admin.setPasswordHash(passwordEncoder.encode(adminProperties.getPassword()));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRoles(Set.of(adminRole));
        return userRepository.save(admin);
    }

    private void ensureAdminHasPlatformAccess(UserAccount admin, Role adminRole) {
        if (admin.getRoles().stream().noneMatch(role -> role.getId().equals(adminRole.getId()))) {
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }
}
