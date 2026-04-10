package com.hisabnikash.erp.masterdata.chartofaccount.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.chartofaccount.domain.AccountType;
import com.hisabnikash.erp.masterdata.chartofaccount.domain.ChartOfAccount;
import com.hisabnikash.erp.masterdata.chartofaccount.dto.ChartOfAccountResponse;
import com.hisabnikash.erp.masterdata.chartofaccount.dto.CreateChartOfAccountRequest;
import com.hisabnikash.erp.masterdata.chartofaccount.dto.UpdateChartOfAccountRequest;
import com.hisabnikash.erp.masterdata.chartofaccount.infrastructure.ChartOfAccountRepository;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChartOfAccountService {

    private final ChartOfAccountRepository repository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_CHART_OF_ACCOUNT")
    public ChartOfAccountResponse create(CreateChartOfAccountRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, request.code())) {
            throw new DuplicateResourceException("Chart of account code already exists: " + request.code());
        }

        validateParent(legalEntityId, request.parentAccountId());

        ChartOfAccount account = new ChartOfAccount();
        apply(account, tenantId, legalEntityId, request.code(), request.name(), request.accountType(), request.parentAccountId(), request.postingAllowed(), request.active());
        ChartOfAccount saved = repository.save(account);
        ChartOfAccountResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getChartOfAccountCreated(),
                "ChartOfAccountCreated",
                "CHART_OF_ACCOUNT",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<ChartOfAccountResponse> getAll() {
        return ownershipService.filterAccessible(repository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChartOfAccount getById(UUID id) {
        ChartOfAccount account = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chart of account not found: " + id));
        return ownershipService.requireReadable(account, "Chart of account");
    }

    @Auditable(action = "UPDATE_CHART_OF_ACCOUNT")
    public ChartOfAccountResponse update(UUID id, UpdateChartOfAccountRequest request) {
        ChartOfAccount account = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (repository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(legalEntityId, request.code(), id)) {
            throw new DuplicateResourceException("Chart of account code already exists: " + request.code());
        }
        if (id.equals(request.parentAccountId())) {
            throw new DuplicateResourceException("Chart of account cannot be its own parent: " + id);
        }

        validateParent(legalEntityId, request.parentAccountId());
        apply(account, tenantId, legalEntityId, request.code(), request.name(), request.accountType(), request.parentAccountId(), request.postingAllowed(), request.active());
        ChartOfAccount saved = repository.save(account);
        ChartOfAccountResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getChartOfAccountUpdated(),
                "ChartOfAccountUpdated",
                "CHART_OF_ACCOUNT",
                saved.getId(),
                response
        );
        return response;
    }

    public ChartOfAccountResponse toResponse(ChartOfAccount account) {
        return new ChartOfAccountResponse(
                account.getId(),
                account.getTenantId(),
                account.getLegalEntityId(),
                account.getCode(),
                account.getName(),
                account.getAccountType(),
                account.getParentAccountId(),
                account.isPostingAllowed(),
                account.isActive(),
                account.getCreatedBy(),
                account.getCreatedAt(),
                account.getUpdatedBy(),
                account.getUpdatedAt()
        );
    }

    private void validateParent(UUID legalEntityId, UUID parentAccountId) {
        if (parentAccountId == null) {
            return;
        }

        ChartOfAccount parent = repository.findById(parentAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent chart of account not found: " + parentAccountId));
        if (!legalEntityId.equals(parent.getLegalEntityId())) {
            throw new IllegalArgumentException("Parent chart of account must belong to the same legal entity");
        }
    }

    private void apply(ChartOfAccount account,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       AccountType accountType,
                       UUID parentAccountId,
                       boolean postingAllowed,
                       boolean active) {
        account.setTenantId(tenantId);
        account.setLegalEntityId(legalEntityId);
        account.setCode(code.trim().toUpperCase());
        account.setName(name.trim());
        account.setAccountType(accountType);
        account.setParentAccountId(parentAccountId);
        account.setPostingAllowed(postingAllowed);
        account.setActive(active);
    }
}
