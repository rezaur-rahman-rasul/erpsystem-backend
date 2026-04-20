package com.hisabnikash.erp.identityaccess.user.application;

import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.organizationaccess.infrastructure.OrganizationAccessAssignmentRepository;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.dto.RoleSummaryResponse;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserResponseAssembler {

    private final OrganizationAccessAssignmentRepository organizationAccessRepository;

    public UserResponse toResponse(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getTenantId(),
                user.getStatus(),
                user.getCreatedBy(),
                user.getCreatedAt(),
                user.getUpdatedBy(),
                user.getUpdatedAt(),
                buildRoleSummaries(user),
                buildOrganizationAccessResponses(user)
        );
    }

    private Set<RoleSummaryResponse> buildRoleSummaries(UserAccount user) {
        Set<RoleSummaryResponse> roles = new LinkedHashSet<>();

        for (Role role : user.getRoles()) {
            roles.add(new RoleSummaryResponse(role.getId(), role.getCode(), role.getName()));
        }

        return roles;
    }

    private Set<OrganizationAccessResponse> buildOrganizationAccessResponses(UserAccount user) {
        Set<OrganizationAccessResponse> organizationAccesses = new LinkedHashSet<>();

        organizationAccessRepository.findByUser_Id(user.getId()).forEach(access ->
                organizationAccesses.add(new OrganizationAccessResponse(
                        access.getId(),
                        user.getId(),
                        access.getLegalEntityId(),
                        access.getBranchId(),
                        access.isPrimaryAccess(),
                        access.getCreatedBy(),
                        access.getCreatedAt(),
                        access.getUpdatedBy(),
                        access.getUpdatedAt()
                ))
        );

        return organizationAccesses;
    }
}
