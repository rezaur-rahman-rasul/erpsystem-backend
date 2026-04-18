package com.hishabnikash.erp.organization.branch.application;

import com.hishabnikash.erp.organization.branch.domain.Branch;
import com.hishabnikash.erp.organization.branch.domain.BranchStatus;
import com.hishabnikash.erp.organization.branch.dto.BranchResponse;
import com.hishabnikash.erp.organization.branch.dto.CreateBranchRequest;
import com.hishabnikash.erp.organization.branch.dto.UpdateBranchRequest;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.branch.mapper.BranchMapper;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.common.exception.InvalidRequestException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hishabnikash.erp.organization.integration.masterdata.infrastructure.WarehouseReferenceRepository;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepository repository;

    @Mock
    private BusinessUnitRepository businessUnitRepository;

    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Mock
    private OrganizationAccessReferenceRepository organizationAccessReferenceRepository;

    @Mock
    private WarehouseReferenceRepository warehouseReferenceRepository;

    private BranchService branchService;
    private RecordingEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new RecordingEventPublisher();

        MessagingProperties messagingProperties = new MessagingProperties();
        messagingProperties.getTopics().setBranchCreated("organization.branch.created");
        messagingProperties.getTopics().setBranchUpdated("organization.branch.updated");

        branchService = new BranchService(
                repository,
                new BranchMapper(),
                eventPublisher,
                messagingProperties,
                businessUnitRepository,
                legalEntityRepository,
                organizationAccessReferenceRepository,
                warehouseReferenceRepository
        );
    }

    @Test
    void createSavesBranchAndPublishesEvent() {
        UUID branchId = UUID.randomUUID();
        UUID legalEntityId = UUID.randomUUID();

        CreateBranchRequest request = new CreateBranchRequest();
        request.setLegalEntityId(legalEntityId);
        request.setCode("BR-01");
        request.setName("Head Office");

        when(repository.existsByCode("BR-01")).thenReturn(false);
        when(legalEntityRepository.existsById(legalEntityId)).thenReturn(true);
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> {
            Branch branch = invocation.getArgument(0);
            branch.setId(branchId);
            return branch;
        });

        BranchResponse created = branchService.create(request);

        ArgumentCaptor<Branch> savedBranch = ArgumentCaptor.forClass(Branch.class);
        verify(repository).save(savedBranch.capture());

        assertThat(savedBranch.getValue().getLegalEntityId()).isEqualTo(legalEntityId);
        assertThat(savedBranch.getValue().getCode()).isEqualTo("BR-01");
        assertThat(savedBranch.getValue().getName()).isEqualTo("Head Office");
        assertThat(savedBranch.getValue().getStatus()).isEqualTo(BranchStatus.ACTIVE);

        assertThat(created.getId()).isEqualTo(branchId);
        assertThat(created.getLegalEntityId()).isEqualTo(legalEntityId);
        assertThat(created.getCode()).isEqualTo("BR-01");
        assertThat(created.getName()).isEqualTo("Head Office");
        assertThat(created.getStatus()).isEqualTo("ACTIVE");

        assertThat(eventPublisher.publishCount).isEqualTo(1);
        assertThat(eventPublisher.topic).isEqualTo("organization.branch.created");
        assertThat(eventPublisher.eventType).isEqualTo("BranchCreated");
        assertThat(eventPublisher.entityType).isEqualTo("BRANCH");
        assertThat(eventPublisher.entityId).isEqualTo(branchId);
        assertThat(eventPublisher.payload).isSameAs(created);
    }

    @Test
    void updateRejectsInactivationWhenActiveWarehousesExist() {
        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setLegalEntityId(UUID.randomUUID());
        branch.setStatus(BranchStatus.ACTIVE);

        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName("Head Office");
        request.setStatus(BranchStatus.INACTIVE);

        when(repository.findById(branchId)).thenReturn(Optional.of(branch));
        when(warehouseReferenceRepository.existsByBranchIdAndActiveTrue(branchId)).thenReturn(true);

        assertThatThrownBy(() -> branchService.update(branchId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Branch cannot be inactivated while active warehouses exist");

        verify(repository, never()).save(any(Branch.class));
        assertThat(eventPublisher.publishCount).isZero();
    }

    private static final class RecordingEventPublisher extends EventPublisher {
        private int publishCount;
        private String topic;
        private String eventType;
        private String entityType;
        private UUID entityId;
        private Object payload;

        private RecordingEventPublisher() {
            super(null, new MessagingProperties());
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
}
