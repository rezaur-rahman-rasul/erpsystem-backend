package com.hishabnikash.erp.organization.infrastructure.messaging;

import com.hishabnikash.erp.organization.common.util.SecurityUtils;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessagingProperties messagingProperties;

    public void publish(String topic,
                        String eventType,
                        String entityType,
                        UUID entityId,
                        Object payload) {
        if (!"kafka".equalsIgnoreCase(messagingProperties.getProvider())) {
            log.debug("Skipping event publish for provider {}", messagingProperties.getProvider());
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

        CompletableFuture.runAsync(() -> send(topic, entityType, entityId, eventType, event));
    }

    private void send(String topic,
                      String entityType,
                      UUID entityId,
                      String eventType,
                      DomainEvent event) {
        try {
            kafkaTemplate.send(topic, event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.warn(
                                    "Failed to publish {} event {} for {} {}: {}",
                                    topic,
                                    eventType,
                                    entityType,
                                    entityId,
                                    error.getMessage()
                            );
                        }
                    });
        } catch (Exception error) {
            log.warn(
                    "Failed to queue {} event {} for {} {}: {}",
                    topic,
                    eventType,
                    entityType,
                    entityId,
                    error.getMessage()
            );
        }
    }
}
