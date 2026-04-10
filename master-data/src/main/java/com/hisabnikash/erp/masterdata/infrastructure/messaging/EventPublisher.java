package com.hisabnikash.erp.masterdata.infrastructure.messaging;

import com.hisabnikash.erp.masterdata.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic,
                        String eventType,
                        String entityType,
                        UUID entityId,
                        Object payload) {
        if (!StringUtils.hasText(topic)) {
            log.debug("Skipping event publish because no topic is configured for eventType={}", eventType);
            return;
        }

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

        try {
            kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("event.publish.failed topic={} eventType={} entityType={} entityId={} error={}",
                            topic,
                            eventType,
                            entityType,
                            entityId,
                            ex.getClass().getSimpleName());
                }
            });
        } catch (RuntimeException ex) {
            log.warn("event.publish.failed topic={} eventType={} entityType={} entityId={} error={}",
                    topic,
                    eventType,
                    entityType,
                    entityId,
                    ex.getClass().getSimpleName());
        }
    }
}
