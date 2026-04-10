package com.hisabnikash.erp.enterprisestructure.hierarchy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DepartmentTreeResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private List<DepartmentTreeResponse> children;
}
