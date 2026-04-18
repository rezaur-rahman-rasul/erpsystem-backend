package com.hishabnikash.erp.organization.infrastructure.messaging;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class DomainEvent {

    private UUID eventId;
    private String eventType;
    private Instant occurredAt;
    private String actorId;
    private UUID entityId;
    private String entityType;
    private String tenantId;
    private Object payload;
}
