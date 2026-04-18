package com.hishabnikash.erp.organization.branch.mapper;

import com.hishabnikash.erp.organization.branch.domain.Branch;
import com.hishabnikash.erp.organization.branch.domain.BranchStatus;
import com.hishabnikash.erp.organization.branch.dto.BranchResponse;
import com.hishabnikash.erp.organization.branch.dto.CreateBranchRequest;
import com.hishabnikash.erp.organization.branch.dto.UpdateBranchRequest;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toEntity(CreateBranchRequest request) {
        Branch branch = new Branch();
        branch.setLegalEntityId(request.getLegalEntityId());
        branch.setBusinessUnitId(request.getBusinessUnitId());
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddressLine1(request.getAddressLine1());
        branch.setAddressLine2(request.getAddressLine2());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setPostalCode(request.getPostalCode());
        branch.setCountryCode(request.getCountryCode());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setTimezone(request.getTimezone());
        branch.setStatus(BranchStatus.ACTIVE);
        return branch;
    }

    public void updateEntity(Branch branch, UpdateBranchRequest request) {
        branch.setBusinessUnitId(request.getBusinessUnitId());
        branch.setName(request.getName());
        branch.setAddressLine1(request.getAddressLine1());
        branch.setAddressLine2(request.getAddressLine2());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setPostalCode(request.getPostalCode());
        branch.setCountryCode(request.getCountryCode());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setTimezone(request.getTimezone());
        branch.setStatus(request.getStatus());
    }

    public BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .legalEntityId(branch.getLegalEntityId())
                .businessUnitId(branch.getBusinessUnitId())
                .code(branch.getCode())
                .name(branch.getName())
                .addressLine1(branch.getAddressLine1())
                .addressLine2(branch.getAddressLine2())
                .city(branch.getCity())
                .state(branch.getState())
                .postalCode(branch.getPostalCode())
                .countryCode(branch.getCountryCode())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .timezone(branch.getTimezone())
                .status(branch.getStatus().name())
                .createdBy(branch.getCreatedBy())
                .createdAt(branch.getCreatedAt())
                .lastUpdatedBy(branch.getUpdatedBy())
                .lastUpdatedAt(branch.getUpdatedAt())
                .build();
    }
}
