package com.hisabnikash.erp.enterprisestructure.hierarchy.application;

import com.hisabnikash.erp.enterprisestructure.branch.domain.Branch;
import com.hisabnikash.erp.enterprisestructure.branch.infrastructure.BranchRepository;
import com.hisabnikash.erp.enterprisestructure.businessunit.domain.BusinessUnit;
import com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure.BusinessUnitRepository;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.department.domain.Department;
import com.hisabnikash.erp.enterprisestructure.department.infrastructure.DepartmentRepository;
import com.hisabnikash.erp.enterprisestructure.department.validation.DepartmentHierarchyValidator;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.BranchTreeResponse;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.BusinessUnitSummaryResponse;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.DepartmentTreeResponse;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.HierarchyValidationRequest;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.HierarchyValidationResponse;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.LocationSummaryResponse;
import com.hisabnikash.erp.enterprisestructure.hierarchy.dto.OrganizationTreeResponse;
import com.hisabnikash.erp.enterprisestructure.legalentity.domain.LegalEntity;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.location.domain.Location;
import com.hisabnikash.erp.enterprisestructure.location.infrastructure.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationHierarchyService {

    private final LegalEntityRepository legalEntityRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final DepartmentHierarchyValidator hierarchyValidator;

    @Cacheable(cacheNames = CacheNames.ORGANIZATION_TREE, key = "'ALL'")
    public List<OrganizationTreeResponse> getAllTrees() {
        return legalEntityRepository.findAll().stream()
                .sorted(Comparator.comparing(LegalEntity::getCode))
                .map(this::buildTree)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.ORGANIZATION_TREE, key = "#legalEntityId")
    public OrganizationTreeResponse getTree(UUID legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + legalEntityId));
        return buildTree(legalEntity);
    }

    public HierarchyValidationResponse validate(HierarchyValidationRequest request) {
        try {
            hierarchyValidator.validateNoCycle(request.getDepartmentId(), request.getProposedParentDepartmentId());
            return HierarchyValidationResponse.builder()
                    .valid(true)
                    .message("Hierarchy is valid")
                    .build();
        } catch (RuntimeException ex) {
            return HierarchyValidationResponse.builder()
                    .valid(false)
                    .message(ex.getMessage())
                    .build();
        }
    }

    private OrganizationTreeResponse buildTree(LegalEntity legalEntity) {
        List<BusinessUnit> businessUnits = businessUnitRepository.findByLegalEntityId(legalEntity.getId());
        List<Branch> branches = branchRepository.findByLegalEntityId(legalEntity.getId());
        List<Department> departments = departmentRepository.findByLegalEntityId(legalEntity.getId());
        List<Location> locations = locationRepository.findByLegalEntityId(legalEntity.getId());

        Map<UUID, List<Location>> locationsByBranch = locations.stream()
                .filter(location -> location.getBranchId() != null)
                .collect(Collectors.groupingBy(Location::getBranchId));
        Map<UUID, List<Department>> departmentsByBranch = departments.stream()
                .filter(department -> department.getBranchId() != null)
                .collect(Collectors.groupingBy(Department::getBranchId));

        return OrganizationTreeResponse.builder()
                .legalEntityId(legalEntity.getId())
                .legalEntityCode(legalEntity.getCode())
                .legalEntityName(legalEntity.getLegalName())
                .status(legalEntity.getStatus().name())
                .businessUnits(businessUnits.stream()
                        .sorted(Comparator.comparing(BusinessUnit::getCode))
                        .map(this::toBusinessUnitSummary)
                        .toList())
                .branches(branches.stream()
                        .sorted(Comparator.comparing(Branch::getCode))
                        .map(branch -> toBranchTree(
                                branch,
                                locationsByBranch.getOrDefault(branch.getId(), List.of()),
                                departmentsByBranch.getOrDefault(branch.getId(), List.of())
                        ))
                        .toList())
                .build();
    }

    private BusinessUnitSummaryResponse toBusinessUnitSummary(BusinessUnit businessUnit) {
        return BusinessUnitSummaryResponse.builder()
                .id(businessUnit.getId())
                .code(businessUnit.getCode())
                .name(businessUnit.getName())
                .status(businessUnit.getStatus().name())
                .build();
    }

    private BranchTreeResponse toBranchTree(Branch branch,
                                            List<Location> locations,
                                            List<Department> departments) {
        return BranchTreeResponse.builder()
                .id(branch.getId())
                .businessUnitId(branch.getBusinessUnitId())
                .code(branch.getCode())
                .name(branch.getName())
                .status(branch.getStatus().name())
                .locations(locations.stream()
                        .sorted(Comparator.comparing(Location::getCode))
                        .map(this::toLocationSummary)
                        .toList())
                .departments(buildDepartmentForest(departments))
                .build();
    }

    private LocationSummaryResponse toLocationSummary(Location location) {
        return LocationSummaryResponse.builder()
                .id(location.getId())
                .code(location.getCode())
                .name(location.getName())
                .type(location.getType().name())
                .status(location.getStatus().name())
                .build();
    }

    private List<DepartmentTreeResponse> buildDepartmentForest(List<Department> departments) {
        if (departments.isEmpty()) {
            return List.of();
        }

        Map<UUID, Department> byId = departments.stream()
                .collect(Collectors.toMap(Department::getId, Function.identity()));
        Map<UUID, List<Department>> childrenByParent = departments.stream()
                .filter(department -> department.getParentDepartmentId() != null)
                .collect(Collectors.groupingBy(Department::getParentDepartmentId));
        Set<UUID> departmentIds = byId.keySet();

        return departments.stream()
                .filter(department -> department.getParentDepartmentId() == null
                        || !departmentIds.contains(department.getParentDepartmentId()))
                .sorted(Comparator.comparing(Department::getCode))
                .map(department -> toDepartmentTree(department, childrenByParent))
                .toList();
    }

    private DepartmentTreeResponse toDepartmentTree(Department department,
                                                    Map<UUID, List<Department>> childrenByParent) {
        List<DepartmentTreeResponse> children = childrenByParent.getOrDefault(department.getId(), List.of()).stream()
                .sorted(Comparator.comparing(Department::getCode))
                .map(child -> toDepartmentTree(child, childrenByParent))
                .toList();

        return DepartmentTreeResponse.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .status(department.getStatus().name())
                .children(children)
                .build();
    }
}
