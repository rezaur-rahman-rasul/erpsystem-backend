package com.hisabnikash.erp.enterprisestructure.location.mapper;

import com.hisabnikash.erp.enterprisestructure.location.domain.Location;
import com.hisabnikash.erp.enterprisestructure.location.domain.LocationStatus;
import com.hisabnikash.erp.enterprisestructure.location.dto.CreateLocationRequest;
import com.hisabnikash.erp.enterprisestructure.location.dto.LocationResponse;
import com.hisabnikash.erp.enterprisestructure.location.dto.UpdateLocationRequest;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(CreateLocationRequest request) {
        Location location = new Location();
        location.setLegalEntityId(request.getLegalEntityId());
        location.setBranchId(request.getBranchId());
        location.setCode(request.getCode());
        location.setName(request.getName());
        location.setType(request.getType());
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setPostalCode(request.getPostalCode());
        location.setCountryCode(request.getCountryCode());
        location.setStatus(LocationStatus.ACTIVE);
        return location;
    }

    public void updateEntity(Location location, UpdateLocationRequest request) {
        location.setBranchId(request.getBranchId());
        location.setName(request.getName());
        location.setType(request.getType());
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setPostalCode(request.getPostalCode());
        location.setCountryCode(request.getCountryCode());
        location.setStatus(request.getStatus());
    }

    public LocationResponse toResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .legalEntityId(location.getLegalEntityId())
                .branchId(location.getBranchId())
                .code(location.getCode())
                .name(location.getName())
                .type(location.getType().name())
                .addressLine1(location.getAddressLine1())
                .addressLine2(location.getAddressLine2())
                .city(location.getCity())
                .state(location.getState())
                .postalCode(location.getPostalCode())
                .countryCode(location.getCountryCode())
                .status(location.getStatus().name())
                .createdBy(location.getCreatedBy())
                .createdAt(location.getCreatedAt())
                .lastUpdatedBy(location.getUpdatedBy())
                .lastUpdatedAt(location.getUpdatedAt())
                .build();
    }
}
