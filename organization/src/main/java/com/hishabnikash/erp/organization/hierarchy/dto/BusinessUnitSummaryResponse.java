package com.hishabnikash.erp.organization.hierarchy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BusinessUnitSummaryResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;
}
