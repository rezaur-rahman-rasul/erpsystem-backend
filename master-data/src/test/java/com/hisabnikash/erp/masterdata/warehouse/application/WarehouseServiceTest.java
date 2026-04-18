package com.hisabnikash.erp.masterdata.warehouse.application;

import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.ownership.BranchOwnership;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.masterdata.warehouse.domain.Warehouse;
import com.hisabnikash.erp.masterdata.warehouse.dto.CreateWarehouseRequest;
import com.hisabnikash.erp.masterdata.warehouse.dto.WarehouseResponse;
import com.hisabnikash.erp.masterdata.warehouse.infrastructure.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository repository;

    @Mock
    private BranchReferenceRepository branchReferenceRepository;

    @Mock
    private OrganizationAccessReferenceRepository organizationAccessRepository;

    @Mock
    private MasterDataOwnershipService ownershipService;

    private WarehouseService warehouseService;
    private RecordingEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new RecordingEventPublisher();

        MessagingProperties messagingProperties = new MessagingProperties();
        messagingProperties.getTopics().setWarehouseCreated("masterdata.warehouse.created");
        messagingProperties.getTopics().setWarehouseUpdated("masterdata.warehouse.updated");

        warehouseService = new WarehouseService(
                repository,
                eventPublisher,
                messagingProperties,
                branchReferenceRepository,
                organizationAccessRepository,
                ownershipService
        );
    }

    @Test
    void createNormalizesWarehouseDataAndPublishesEvent() {
        UUID warehouseId = UUID.randomUUID();
        UUID legalEntityId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "wh-01",
                " Main Warehouse ",
                branchId,
                " LOC-1 ",
                true
        );

        when(ownershipService.requireAccessibleBranch(branchId))
                .thenReturn(new BranchOwnership("TENANT-1", legalEntityId, branchId));
        when(repository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, "wh-01")).thenReturn(false);
        when(repository.save(any(Warehouse.class))).thenAnswer(invocation -> {
            Warehouse warehouse = invocation.getArgument(0);
            warehouse.setId(warehouseId);
            return warehouse;
        });

        WarehouseResponse response = warehouseService.create(request);

        ArgumentCaptor<Warehouse> savedWarehouse = ArgumentCaptor.forClass(Warehouse.class);
        verify(repository).save(savedWarehouse.capture());

        assertThat(savedWarehouse.getValue().getTenantId()).isEqualTo("TENANT-1");
        assertThat(savedWarehouse.getValue().getLegalEntityId()).isEqualTo(legalEntityId);
        assertThat(savedWarehouse.getValue().getBranchId()).isEqualTo(branchId);
        assertThat(savedWarehouse.getValue().getCode()).isEqualTo("WH-01");
        assertThat(savedWarehouse.getValue().getName()).isEqualTo("Main Warehouse");
        assertThat(savedWarehouse.getValue().getLocationCode()).isEqualTo("LOC-1");
        assertThat(savedWarehouse.getValue().isActive()).isTrue();

        assertThat(response.id()).isEqualTo(warehouseId);
        assertThat(response.tenantId()).isEqualTo("TENANT-1");
        assertThat(response.legalEntityId()).isEqualTo(legalEntityId);
        assertThat(response.code()).isEqualTo("WH-01");
        assertThat(response.name()).isEqualTo("Main Warehouse");

        assertThat(eventPublisher.publishCount).isEqualTo(1);
        assertThat(eventPublisher.topic).isEqualTo("masterdata.warehouse.created");
        assertThat(eventPublisher.eventType).isEqualTo("WarehouseCreated");
        assertThat(eventPublisher.entityType).isEqualTo("WAREHOUSE");
        assertThat(eventPublisher.entityId).isEqualTo(warehouseId);
        assertThat(eventPublisher.payload).isSameAs(response);
    }

    @Test
    void createRejectsDuplicateWarehouseCode() {
        UUID legalEntityId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "WH-01",
                "Main Warehouse",
                branchId,
                null,
                true
        );

        when(ownershipService.requireAccessibleBranch(branchId))
                .thenReturn(new BranchOwnership("TENANT-1", legalEntityId, branchId));
        when(repository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, "WH-01")).thenReturn(true);

        assertThatThrownBy(() -> warehouseService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Warehouse code already exists: WH-01");

        verify(repository, never()).save(any(Warehouse.class));
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
            super(null);
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
