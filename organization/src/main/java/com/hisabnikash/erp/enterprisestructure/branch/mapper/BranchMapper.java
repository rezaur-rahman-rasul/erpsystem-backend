package com.hisabnikash.erp.enterprisestructure.branch.mapper;

import com.hisabnikash.erp.enterprisestructure.branch.domain.Branch;
import com.hisabnikash.erp.enterprisestructure.branch.domain.BranchStatus;
import com.hisabnikash.erp.enterprisestructure.branch.dto.BranchResponse;
import com.hisabnikash.erp.enterprisestructure.branch.dto.CreateBranchRequest;
import com.hisabnikash.erp.enterprisestructure.branch.dto.UpdateBranchRequest;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toEntity(CreateBranchRequest req) {
        Branch b = new Branch();
        b.setLegalEntityId(req.getLegalEntityId());
        b.setBusinessUnitId(req.getBusinessUnitId());
        b.setCode(req.getCode());
        b.setName(req.getName());
        b.setAddressLine1(req.getAddressLine1());
        b.setAddressLine2(req.getAddressLine2());
        b.setCity(req.getCity());
        b.setState(req.getState());
        b.setPostalCode(req.getPostalCode());
        b.setCountryCode(req.getCountryCode());
        b.setPhone(req.getPhone());
        b.setEmail(req.getEmail());
        b.setTimezone(req.getTimezone());
        b.setStatus(BranchStatus.ACTIVE);
        return b;
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

    public BranchResponse toResponse(Branch b) {
        return BranchResponse.builder()
                .id(b.getId())
                .legalEntityId(b.getLegalEntityId())
                .businessUnitId(b.getBusinessUnitId())
                .code(b.getCode())
                .name(b.getName())
                .addressLine1(b.getAddressLine1())
                .addressLine2(b.getAddressLine2())
                .city(b.getCity())
                .state(b.getState())
                .postalCode(b.getPostalCode())
                .countryCode(b.getCountryCode())
                .phone(b.getPhone())
                .email(b.getEmail())
                .timezone(b.getTimezone())
                .status(b.getStatus().name())
                .createdBy(b.getCreatedBy())
                .createdAt(b.getCreatedAt())
                .lastUpdatedBy(b.getUpdatedBy())
                .lastUpdatedAt(b.getUpdatedAt())
                .build();
    }
}
