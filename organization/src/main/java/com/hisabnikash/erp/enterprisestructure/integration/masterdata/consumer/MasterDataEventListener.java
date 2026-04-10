package com.hisabnikash.erp.enterprisestructure.integration.masterdata.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.DomainEvent;
import com.hisabnikash.erp.enterprisestructure.integration.masterdata.domain.WarehouseReference;
import com.hisabnikash.erp.enterprisestructure.integration.masterdata.dto.WarehouseEventPayload;
import com.hisabnikash.erp.enterprisestructure.integration.masterdata.infrastructure.WarehouseReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterDataEventListener {

    private final ObjectMapper objectMapper;
    private final WarehouseReferenceRepository warehouseRepository;

    @KafkaListener(topics = {"master-data.warehouse.created", "master-data.warehouse.updated"})
    @Transactional
    public void onWarehouseEvent(DomainEvent event) {
        WarehouseEventPayload payload = objectMapper.convertValue(event.getPayload(), WarehouseEventPayload.class);
        WarehouseReference reference = warehouseRepository.findById(payload.id())
                .orElseGet(WarehouseReference::new);
        reference.setId(payload.id());
        reference.setBranchId(payload.branchId());
        reference.setCode(payload.code());
        reference.setName(payload.name());
        reference.setActive(payload.active());
        warehouseRepository.save(reference);
        log.debug("master-data.warehouse.synced id={} eventType={}", payload.id(), event.getEventType());
    }
}
