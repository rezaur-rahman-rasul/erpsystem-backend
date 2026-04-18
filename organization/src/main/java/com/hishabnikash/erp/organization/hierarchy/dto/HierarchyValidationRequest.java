package com.hishabnikash.erp.organization.hierarchy.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HierarchyValidationRequest {

    private UUID departmentId;
    private UUID proposedParentDepartmentId;
}
