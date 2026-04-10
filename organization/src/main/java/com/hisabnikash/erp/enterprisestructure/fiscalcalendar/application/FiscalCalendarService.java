package com.hisabnikash.erp.enterprisestructure.fiscalcalendar.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.domain.FiscalCalendar;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.CreateFiscalCalendarRequest;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.FiscalCalendarResponse;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.dto.UpdateFiscalCalendarRequest;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.infrastructure.FiscalCalendarRepository;
import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.mapper.FiscalCalendarMapper;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FiscalCalendarService {

    private final FiscalCalendarRepository repository;
    private final FiscalCalendarMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_FISCAL_CALENDAR")
    public FiscalCalendarResponse create(CreateFiscalCalendarRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Fiscal calendar code already exists: " + request.getCode());
        }

        validateDateRange(request.getStartDate(), request.getEndDate());
        validateLegalEntity(request.getLegalEntityId());

        FiscalCalendar saved = repository.save(mapper.toEntity(request));
        FiscalCalendarResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getFiscalCalendarCreated(),
                "FiscalCalendarCreated",
                "FISCAL_CALENDAR",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public Page<FiscalCalendarResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<FiscalCalendar> page = legalEntityId == null
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FiscalCalendarResponse getById(UUID id) {
        FiscalCalendar fiscalCalendar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal calendar not found: " + id));
        return mapper.toResponse(fiscalCalendar);
    }

    @Auditable(action = "UPDATE_FISCAL_CALENDAR")
    public FiscalCalendarResponse update(UUID id, UpdateFiscalCalendarRequest request) {
        FiscalCalendar fiscalCalendar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal calendar not found: " + id));

        validateDateRange(request.getStartDate(), request.getEndDate());
        mapper.updateEntity(fiscalCalendar, request);
        FiscalCalendar saved = repository.save(fiscalCalendar);
        FiscalCalendarResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getFiscalCalendarUpdated(),
                "FiscalCalendarUpdated",
                "FISCAL_CALENDAR",
                saved.getId(),
                response
        );
        return response;
    }

    private void validateLegalEntity(UUID legalEntityId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new DuplicateResourceException("Fiscal calendar start date must be before or equal to end date");
        }
    }
}
