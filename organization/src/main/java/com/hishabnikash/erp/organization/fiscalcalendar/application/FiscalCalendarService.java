package com.hishabnikash.erp.organization.fiscalcalendar.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.fiscalcalendar.domain.FiscalCalendar;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.CreateFiscalCalendarRequest;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.FiscalCalendarResponse;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.UpdateFiscalCalendarRequest;
import com.hishabnikash.erp.organization.fiscalcalendar.infrastructure.FiscalCalendarRepository;
import com.hishabnikash.erp.organization.fiscalcalendar.mapper.FiscalCalendarMapper;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
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

    private final FiscalCalendarRepository fiscalCalendarRepository;
    private final FiscalCalendarMapper fiscalCalendarMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_FISCAL_CALENDAR")
    public FiscalCalendarResponse create(CreateFiscalCalendarRequest request) {
        if (fiscalCalendarRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Fiscal calendar code already exists: " + request.getCode());
        }

        validateDateRange(request.getStartDate(), request.getEndDate());
        validateLegalEntity(request.getLegalEntityId());

        FiscalCalendar saved = fiscalCalendarRepository.save(fiscalCalendarMapper.toEntity(request));
        FiscalCalendarResponse response = fiscalCalendarMapper.toResponse(saved);
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
                ? fiscalCalendarRepository.findAll(pageable)
                : fiscalCalendarRepository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(fiscalCalendarMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FiscalCalendarResponse getById(UUID id) {
        FiscalCalendar fiscalCalendar = fiscalCalendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal calendar not found: " + id));
        return fiscalCalendarMapper.toResponse(fiscalCalendar);
    }

    @Auditable(action = "UPDATE_FISCAL_CALENDAR")
    public FiscalCalendarResponse update(UUID id, UpdateFiscalCalendarRequest request) {
        FiscalCalendar fiscalCalendar = fiscalCalendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal calendar not found: " + id));

        validateDateRange(request.getStartDate(), request.getEndDate());
        fiscalCalendarMapper.updateEntity(fiscalCalendar, request);
        FiscalCalendar saved = fiscalCalendarRepository.save(fiscalCalendar);
        FiscalCalendarResponse response = fiscalCalendarMapper.toResponse(saved);
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
