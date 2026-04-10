package com.hisabnikash.erp.enterprisestructure.hierarchy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LocationSummaryResponse {

    private UUID id;
    private String code;
    private String name;
    private String type;
    private String status;
}
