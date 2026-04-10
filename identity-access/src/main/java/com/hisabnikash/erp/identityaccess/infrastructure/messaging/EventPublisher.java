package com.hisabnikash.erp.identityaccess.infrastructure.messaging;

import com.hisabnikash.erp.identityaccess.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaOperations<String, Object> kafkaTemplate;
    @Qualifier("eventPublisherTaskExecutor")
    private final TaskExecutor eventPublisherTaskExecutor;

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

        Runnable publishTask = () -> doPublish(topic, eventType, entityType, entityId, event);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatch(topic, eventType, entityType, entityId, publishTask);
                }
            });
            return;
        }

        dispatch(topic, eventType, entityType, entityId, publishTask);
    }

    private void dispatch(String topic,
                          String eventType,
                          String entityType,
                          UUID entityId,
                          Runnable publishTask) {
        try {
            eventPublisherTaskExecutor.execute(publishTask);
        } catch (RuntimeException ex) {
            log.warn("event.publish.dispatch.failed topic={} eventType={} entityType={} entityId={} error={}",
                    topic,
                    eventType,
                    entityType,
                    entityId,
                    ex.getClass().getSimpleName());
        }
    }

    private void doPublish(String topic,
                           String eventType,
                           String entityType,
                           UUID entityId,
                           DomainEvent event) {
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
