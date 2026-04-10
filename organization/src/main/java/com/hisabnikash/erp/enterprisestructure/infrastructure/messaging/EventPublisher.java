package com.hisabnikash.erp.enterprisestructure.infrastructure.messaging;

import com.hisabnikash.erp.enterprisestructure.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic,
                        String eventType,
                        String entityType,
                        UUID entityId,
                        Object payload) {
        DomainEvent event = DomainEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .occurredAt(Instant.now())
                .actorId(SecurityUtils.getCurrentUserIdOrSystem())
                .tenantId(SecurityUtils.getCurrentTenantId())
                .entityId(entityId)
                .entityType(entityType)
                .payload(payload)
                .build();

        kafkaTemplate.send(topic, event);
    }
}
