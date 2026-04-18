package com.hishabnikash.erp.organization.fiscalcalendar.mapper;

import com.hishabnikash.erp.organization.fiscalcalendar.domain.FiscalCalendar;
import com.hishabnikash.erp.organization.fiscalcalendar.domain.FiscalCalendarStatus;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.CreateFiscalCalendarRequest;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.FiscalCalendarResponse;
import com.hishabnikash.erp.organization.fiscalcalendar.dto.UpdateFiscalCalendarRequest;
import org.springframework.stereotype.Component;

@Component
public class FiscalCalendarMapper {

    public FiscalCalendar toEntity(CreateFiscalCalendarRequest request) {
        FiscalCalendar fiscalCalendar = new FiscalCalendar();
        fiscalCalendar.setLegalEntityId(request.getLegalEntityId());
        fiscalCalendar.setCode(request.getCode());
        fiscalCalendar.setName(request.getName());
        fiscalCalendar.setStartDate(request.getStartDate());
        fiscalCalendar.setEndDate(request.getEndDate());
        fiscalCalendar.setStatus(FiscalCalendarStatus.PLANNED);
        return fiscalCalendar;
    }

    public void updateEntity(FiscalCalendar fiscalCalendar, UpdateFiscalCalendarRequest request) {
        fiscalCalendar.setName(request.getName());
        fiscalCalendar.setStartDate(request.getStartDate());
        fiscalCalendar.setEndDate(request.getEndDate());
        fiscalCalendar.setStatus(request.getStatus());
    }

    public FiscalCalendarResponse toResponse(FiscalCalendar fiscalCalendar) {
        return FiscalCalendarResponse.builder()
                .id(fiscalCalendar.getId())
                .legalEntityId(fiscalCalendar.getLegalEntityId())
                .code(fiscalCalendar.getCode())
                .name(fiscalCalendar.getName())
                .startDate(fiscalCalendar.getStartDate())
                .endDate(fiscalCalendar.getEndDate())
                .status(fiscalCalendar.getStatus().name())
                .createdBy(fiscalCalendar.getCreatedBy())
                .createdAt(fiscalCalendar.getCreatedAt())
                .lastUpdatedBy(fiscalCalendar.getUpdatedBy())
                .lastUpdatedAt(fiscalCalendar.getUpdatedAt())
                .build();
    }
}
