package com.hisabnikash.erp.identityaccess.user.application;

import com.hisabnikash.erp.identityaccess.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.identityaccess.config.properties.MessagingProperties;
import com.hisabnikash.erp.identityaccess.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.TenantProfileReferenceRepository;
import com.hisabnikash.erp.identityaccess.organizationaccess.infrastructure.OrganizationAccessAssignmentRepository;
import com.hisabnikash.erp.identityaccess.role.application.RoleService;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import com.hisabnikash.erp.identityaccess.user.dto.CreateUserRequest;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrganizationAccessAssignmentRepository organizationAccessRepository;

    @Mock
    private TenantProfileReferenceRepository tenantProfileReferenceRepository;

    private UserService userService;
    private StubRoleService roleService;
    private RecordingEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        roleService = new StubRoleService();
        eventPublisher = new RecordingEventPublisher();

        MessagingProperties messagingProperties = new MessagingProperties();
        messagingProperties.getTopics().setUserCreated("identity.user.created");
        messagingProperties.getTopics().setUserUpdated("identity.user.updated");
        messagingProperties.getTopics().setUserStatusChanged("identity.user.status.changed");

        userService = new UserService(
                userRepository,
                roleService,
                passwordEncoder,
                eventPublisher,
                messagingProperties,
                organizationAccessRepository,
                tenantProfileReferenceRepository
        );
    }

    @Test
    void createNormalizesUserDataAndPublishesEvent() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setCode("ADMIN");
        role.setName("Administrator");
        roleService.register(role);

        CreateUserRequest request = new CreateUserRequest(
                "  admin  ",
                " Admin@Example.com ",
                "  Admin User  ",
                "secret123",
                " TENANT-1 ",
                Set.of(roleId)
        );

        when(userRepository.existsByUsernameIgnoreCase("admin")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("admin@example.com")).thenReturn(false);
        when(tenantProfileReferenceRepository.count()).thenReturn(1L);
        when(tenantProfileReferenceRepository.existsByTenantCodeIgnoreCaseAndActiveTrue("TENANT-1"))
                .thenReturn(true);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });
        when(organizationAccessRepository.findByUser_Id(userId)).thenReturn(List.of());

        UserResponse response = userService.create(request);

        ArgumentCaptor<UserAccount> savedUser = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(savedUser.capture());

        assertThat(savedUser.getValue().getUsername()).isEqualTo("admin");
        assertThat(savedUser.getValue().getEmail()).isEqualTo("admin@example.com");
        assertThat(savedUser.getValue().getDisplayName()).isEqualTo("Admin User");
        assertThat(savedUser.getValue().getTenantId()).isEqualTo("TENANT-1");
        assertThat(savedUser.getValue().getPasswordHash()).isEqualTo("encoded-password");
        assertThat(savedUser.getValue().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(savedUser.getValue().getRoles()).containsExactly(role);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.email()).isEqualTo("admin@example.com");
        assertThat(response.roles()).singleElement().satisfies(roleSummary -> {
            assertThat(roleSummary.code()).isEqualTo("ADMIN");
            assertThat(roleSummary.name()).isEqualTo("Administrator");
        });
        assertThat(response.organizationAccesses()).isEmpty();

        assertThat(eventPublisher.publishCount).isEqualTo(1);
        assertThat(eventPublisher.topic).isEqualTo("identity.user.created");
        assertThat(eventPublisher.eventType).isEqualTo("UserCreated");
        assertThat(eventPublisher.entityType).isEqualTo("USER");
        assertThat(eventPublisher.entityId).isEqualTo(userId);
        assertThat(eventPublisher.payload).isSameAs(response);
        assertThat(roleService.lookupCount).isEqualTo(1);
    }

    @Test
    void createRejectsDuplicateUsernameBeforeSaving() {
        CreateUserRequest request = new CreateUserRequest(
                "admin",
                "admin@example.com",
                "Admin User",
                "secret123",
                "TENANT-1",
                Set.of(UUID.randomUUID())
        );

        when(userRepository.existsByUsernameIgnoreCase("admin")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists: admin");

        verify(userRepository, never()).save(any(UserAccount.class));
        assertThat(eventPublisher.publishCount).isZero();
        assertThat(roleService.lookupCount).isZero();
    }

    private static final class RecordingEventPublisher extends EventPublisher {
        private int publishCount;
        private String topic;
        private String eventType;
        private String entityType;
        private UUID entityId;
        private Object payload;

        private RecordingEventPublisher() {
            super(null, null);
        }

        @Override
        public void publish(String topic, String eventType, String entityType, UUID entityId, Object payload) {
            publishCount++;
            this.topic = topic;
            this.eventType = eventType;
            this.entityType = entityType;
            this.entityId = entityId;
            this.payload = payload;
        }
    }

    private static final class StubRoleService extends RoleService {
        private final Map<UUID, Role> rolesById = new HashMap<>();
        private int lookupCount;

        private StubRoleService() {
            super(null, null, null, null);
        }

        @Override
        public Role getById(UUID id) {
            lookupCount++;
            return rolesById.get(id);
        }

        private void register(Role role) {
            rolesById.put(role.getId(), role);
        }
    }
}
