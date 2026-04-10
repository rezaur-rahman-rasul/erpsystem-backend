package com.hisabnikash.erp.enterprisestructure.subsidiary.infrastructure;

import com.hisabnikash.erp.enterprisestructure.subsidiary.domain.Subsidiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, UUID> {

    boolean existsByCode(String code);

    Page<Subsidiary> findByParentLegalEntityId(UUID parentLegalEntityId, Pageable pageable);
}
