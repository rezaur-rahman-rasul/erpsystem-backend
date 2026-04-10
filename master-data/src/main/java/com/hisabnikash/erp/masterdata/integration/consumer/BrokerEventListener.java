package com.hisabnikash.erp.masterdata.integration.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.DomainEvent;
import com.hisabnikash.erp.masterdata.integration.identity.domain.OrganizationAccessReference;
import com.hisabnikash.erp.masterdata.integration.identity.dto.OrganizationAccessEventPayload;
import com.hisabnikash.erp.masterdata.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.domain.BranchReference;
import com.hisabnikash.erp.masterdata.integration.organization.domain.LegalEntityReference;
import com.hisabnikash.erp.masterdata.integration.organization.dto.BranchEventPayload;
import com.hisabnikash.erp.masterdata.integration.organization.dto.LegalEntityEventPayload;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.LegalEntityReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrokerEventListener {

    private final ObjectMapper objectMapper;
    private final LegalEntityReferenceRepository legalEntityRepository;
    private final BranchReferenceRepository branchRepository;
    private final OrganizationAccessReferenceRepository organizationAccessRepository;

    @KafkaListener(topics = {"enterprise.legal-entity.created", "enterprise.legal-entity.updated"})
    @Transactional
    public void onLegalEntityEvent(DomainEvent event) {
        LegalEntityEventPayload payload = objectMapper.convertValue(event.getPayload(), LegalEntityEventPayload.class);
        LegalEntityReference reference = legalEntityRepository.findById(payload.id())
                .orElseGet(LegalEntityReference::new);
        reference.setId(payload.id());
        reference.setCode(payload.code());
        reference.setName(payload.legalName());
        reference.setActive("ACTIVE".equalsIgnoreCase(payload.status()));
        legalEntityRepository.save(reference);
        log.debug("organization.legal-entity.synced id={} eventType={}", payload.id(), event.getEventType());
    }

    @KafkaListener(topics = {"enterprise.branch.created", "enterprise.branch.updated"})
    @Transactional
    public void onBranchEvent(DomainEvent event) {
        BranchEventPayload payload = objectMapper.convertValue(event.getPayload(), BranchEventPayload.class);
        BranchReference reference = branchRepository.findById(payload.id())
                .orElseGet(BranchReference::new);
        reference.setId(payload.id());
        reference.setLegalEntityId(payload.legalEntityId());
        reference.setCode(payload.code());
        reference.setName(payload.name());
        reference.setActive("ACTIVE".equalsIgnoreCase(payload.status()));
        branchRepository.save(reference);
        log.debug("organization.branch.synced id={} eventType={}", payload.id(), event.getEventType());
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
