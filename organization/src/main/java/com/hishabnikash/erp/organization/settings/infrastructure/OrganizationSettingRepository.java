package com.hishabnikash.erp.organization.settings.infrastructure;

import com.hishabnikash.erp.organization.settings.domain.OrganizationSetting;
import com.hishabnikash.erp.organization.settings.domain.SettingOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationSettingRepository extends JpaRepository<OrganizationSetting, UUID> {

    Optional<OrganizationSetting> findByOwnerTypeAndOwnerId(SettingOwnerType ownerType, UUID ownerId);

    boolean existsByOwnerTypeAndOwnerId(SettingOwnerType ownerType, UUID ownerId);
}
