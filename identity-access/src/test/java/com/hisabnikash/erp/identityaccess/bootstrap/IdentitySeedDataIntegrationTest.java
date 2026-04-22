package com.hisabnikash.erp.identityaccess.bootstrap;

import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.role.infrastructure.RoleRepository;
import com.hisabnikash.erp.identityaccess.security.permission.PhaseOnePermissions;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class IdentitySeedDataIntegrationTest {

    @Autowired
    private IdentitySeedData identitySeedData;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    void rerunRestoresPlatformAdminStateAndConfiguredCredentials() throws Exception {
        Role adminRole = roleRepository.findByCodeIgnoreCase("PLATFORM_ADMIN").orElseThrow();
        UserAccount adminUser = userAccountRepository.findByUsernameIgnoreCase("admin").orElseThrow();

        adminRole.setName("Old Platform Administrator");
        adminRole.setDescription("outdated");
        adminRole.setPermissions(new LinkedHashSet<>(Set.of(PhaseOnePermissions.IDENTITY_USER_VIEW)));
        adminUser.setEmail("legacy-admin@erp.local");
        adminUser.setDisplayName("Legacy Admin");
        adminUser.setTenantId("LEGACY");
        adminUser.setStatus(UserStatus.INACTIVE);
        adminUser.setPasswordHash(passwordEncoder.encode("LegacyPassword1!"));
        adminUser.getRoles().clear();

        roleRepository.save(adminRole);
        userAccountRepository.save(adminUser);

        identitySeedData.run();

        Role refreshedRole = roleRepository.findByCodeIgnoreCase("PLATFORM_ADMIN").orElseThrow();
        UserAccount refreshedAdmin = userAccountRepository.findByUsernameIgnoreCase("admin").orElseThrow();

        assertThat(refreshedRole.getName()).isEqualTo("Platform Administrator");
        assertThat(refreshedRole.getDescription()).isEqualTo("Full access across Phase 1 services");
        assertThat(refreshedRole.getPermissions()).containsExactlyInAnyOrderElementsOf(PhaseOnePermissions.all());
        assertThat(refreshedAdmin.getEmail()).isEqualTo("admin@erp.local");
        assertThat(refreshedAdmin.getDisplayName()).isEqualTo("Platform Admin");
        assertThat(refreshedAdmin.getTenantId()).isEqualTo("ERP-DEFAULT");
        assertThat(refreshedAdmin.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(passwordEncoder.matches("ChangeMe123!", refreshedAdmin.getPasswordHash())).isTrue();
        assertThat(refreshedAdmin.getRoles())
                .extracting(Role::getCode)
                .contains("PLATFORM_ADMIN");
    }
}
