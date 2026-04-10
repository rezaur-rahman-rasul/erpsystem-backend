package com.hisabnikash.erp.identityaccess.integration.organization.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisabnikash.erp.identityaccess.infrastructure.messaging.DomainEvent;
import com.hisabnikash.erp.identityaccess.integration.organization.domain.BranchReference;
import com.hisabnikash.erp.identityaccess.integration.organization.domain.LegalEntityReference;
import com.hisabnikash.erp.identityaccess.integration.organization.domain.TenantProfileReference;
import com.hisabnikash.erp.identityaccess.integration.organization.dto.BranchEventPayload;
import com.hisabnikash.erp.identityaccess.integration.organization.dto.LegalEntityEventPayload;
import com.hisabnikash.erp.identityaccess.integration.organization.dto.TenantProfileEventPayload;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.LegalEntityReferenceRepository;
import com.hisabnikash.erp.identityaccess.integration.organization.infrastructure.TenantProfileReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationEventListener {

    private final ObjectMapper objectMapper;
    private final LegalEntityReferenceRepository legalEntityRepository;
    private final BranchReferenceRepository branchRepository;
    private final TenantProfileReferenceRepository tenantProfileRepository;

    @KafkaListener(topics = {"enterprise.legal-entity.created", "enterprise.legal-entity.updated"})
    @Transactional
    public void onLegalEntityEvent(DomainEvent event) {
        LegalEntityEventPayload payload = objectMapper.convertValue(event.getPayload(), LegalEntityEventPayload.class);
        LegalEntityReference reference = legalEntityRepository.findById(payload.id())
                .orElseGet(LegalEntityReference::new);
        reference.setId(payload.id());
        reference.setCode(payload.code());
        reference.setLegalName(payload.legalName());
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

    @KafkaListener(topics = {"enterprise.tenant-profile.created", "enterprise.tenant-profile.updated"})
    @Transactional
    public void onTenantProfileEvent(DomainEvent event) {
        TenantProfileEventPayload payload = objectMapper.convertValue(event.getPayload(), TenantProfileEventPayload.class);
        TenantProfileReference reference = tenantProfileRepository.findById(payload.id())
                .orElseGet(TenantProfileReference::new);
        reference.setId(payload.id());
        reference.setTenantCode(payload.tenantCode());
        reference.setLegalEntityId(payload.legalEntityId());
        reference.setCompanyName(payload.companyName());
        reference.setActive(payload.active());
        tenantProfileRepository.save(reference);
        log.debug("organization.tenant-profile.synced id={} eventType={}", payload.id(), event.getEventType());
    }
}
