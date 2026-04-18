package com.hishabnikash.erp.organization.hierarchy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HierarchyValidationResponse {

    private boolean valid;
    private String message;
}
