package com.hishabnikash.erp.organization.integration.identity.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hishabnikash.erp.organization.infrastructure.messaging.DomainEvent;
import com.hishabnikash.erp.organization.integration.identity.domain.OrganizationAccessReference;
import com.hishabnikash.erp.organization.integration.identity.domain.UserReference;
import com.hishabnikash.erp.organization.integration.identity.dto.OrganizationAccessEventPayload;
import com.hishabnikash.erp.organization.integration.identity.dto.UserEventPayload;
import com.hishabnikash.erp.organization.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hishabnikash.erp.organization.integration.identity.infrastructure.UserReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityAccessEventListener {

    private final ObjectMapper objectMapper;
    private final UserReferenceRepository userRepository;
    private final OrganizationAccessReferenceRepository organizationAccessRepository;

    @KafkaListener(topics = {"identity.user.created", "identity.user.status-changed"})
    @Transactional
    public void onUserEvent(DomainEvent event) {
        UserEventPayload payload = objectMapper.convertValue(event.getPayload(), UserEventPayload.class);
        UserReference reference = userRepository.findById(payload.id())
                .orElseGet(UserReference::new);
        reference.setId(payload.id());
        reference.setUsername(payload.username());
        reference.setEmail(payload.email());
        reference.setDisplayName(payload.displayName());
        reference.setTenantId(payload.tenantId());
        reference.setActive("ACTIVE".equalsIgnoreCase(payload.status()));
        userRepository.save(reference);
        log.debug("identity.user.synced id={} eventType={}", payload.id(), event.getEventType());
    }

    @KafkaListener(topics = {"identity.organization-access.created", "identity.organization-access.updated"})
    @Transactional
    public void onOrganizationAccessEvent(DomainEvent event) {
        OrganizationAccessEventPayload payload = objectMapper.convertValue(event.getPayload(), OrganizationAccessEventPayload.class);
        OrganizationAccessReference reference = organizationAccessRepository.findById(payload.id())
                .orElseGet(OrganizationAccessReference::new);
        reference.setId(payload.id());
        reference.setUserId(payload.userId());
        reference.setLegalEntityId(payload.legalEntityId());
        reference.setBranchId(payload.branchId());
        reference.setPrimaryAccess(payload.primaryAccess());
        organizationAccessRepository.save(reference);
        log.debug("identity.organization-access.synced id={} eventType={}", payload.id(), event.getEventType());
    }
}
