package com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure;

import com.hisabnikash.erp.enterprisestructure.legalentity.domain.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LegalEntityRepository extends JpaRepository<LegalEntity, UUID> {

    boolean existsByCode(String code);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, UUID id);

    Optional<LegalEntity> findByCode(String code);
}
