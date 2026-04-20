package com.hishabnikash.erp.organization.hierarchy.application;

import com.hishabnikash.erp.organization.branch.domain.Branch;
import com.hishabnikash.erp.organization.branch.infrastructure.BranchRepository;
import com.hishabnikash.erp.organization.businessunit.domain.BusinessUnit;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.department.domain.Department;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import com.hishabnikash.erp.organization.department.validation.DepartmentHierarchyValidator;
import com.hishabnikash.erp.organization.hierarchy.dto.BranchTreeResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.BusinessUnitSummaryResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.DepartmentTreeResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.HierarchyValidationRequest;
import com.hishabnikash.erp.organization.hierarchy.dto.HierarchyValidationResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.LocationSummaryResponse;
import com.hishabnikash.erp.organization.hierarchy.dto.OrganizationTreeResponse;
import com.hishabnikash.erp.organization.legalentity.domain.LegalEntity;
import com.hishabnikash.erp.organization.legalentity.dto.LegalEntityResponse;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.legalentity.application.LegalEntityService;
import com.hishabnikash.erp.organization.location.domain.Location;
import com.hishabnikash.erp.organization.location.infrastructure.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final LegalEntityService legalEntityService;

    @Cacheable(cacheNames = CacheNames.ORGANIZATION_TREE, key = "'ALL'", sync = true)
    public List<OrganizationTreeResponse> getAllTrees() {
        List<LegalEntity> legalEntities = new ArrayList<>(legalEntityRepository.findAll());
        legalEntities.sort(Comparator.comparing(LegalEntity::getCode));

        List<OrganizationTreeResponse> trees = new ArrayList<>();
        for (LegalEntity legalEntity : legalEntities) {
            trees.add(buildTree(legalEntity));
        }

        return trees;
    }

    @Cacheable(cacheNames = CacheNames.ORGANIZATION_TREE, key = "#legalEntityId", sync = true)
    public OrganizationTreeResponse getTree(UUID legalEntityId) {
        LegalEntityResponse legalEntity = legalEntityService.getById(legalEntityId);
        return buildTree(legalEntity);
    }

    public HierarchyValidationResponse validate(HierarchyValidationRequest request) {
        try {
            hierarchyValidator.validateNoCycle(
                    request.getDepartmentId(),
                    request.getProposedParentDepartmentId()
            );

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

        return buildTreeSnapshot(
                legalEntity.getId(),
                legalEntity.getCode(),
                legalEntity.getLegalName(),
                legalEntity.getStatus().name(),
                businessUnits,
                branches,
                departments,
                locations
        );
    }

    private OrganizationTreeResponse buildTree(LegalEntityResponse legalEntity) {
        List<BusinessUnit> businessUnits = businessUnitRepository.findByLegalEntityId(legalEntity.getId());
        List<Branch> branches = branchRepository.findByLegalEntityId(legalEntity.getId());
        List<Department> departments = departmentRepository.findByLegalEntityId(legalEntity.getId());
        List<Location> locations = locationRepository.findByLegalEntityId(legalEntity.getId());

        return buildTreeSnapshot(
                legalEntity.getId(),
                legalEntity.getCode(),
                legalEntity.getLegalName(),
                legalEntity.getStatus(),
                businessUnits,
                branches,
                departments,
                locations
        );
    }

    private OrganizationTreeResponse buildTreeSnapshot(
            UUID legalEntityId,
            String legalEntityCode,
            String legalEntityName,
            String status,
            List<BusinessUnit> businessUnits,
            List<Branch> branches,
            List<Department> departments,
            List<Location> locations
    ) {

        Map<UUID, List<Location>> locationsByBranch = groupLocationsByBranch(locations);
        Map<UUID, List<Department>> departmentsByBranch = groupDepartmentsByBranch(departments);

        return OrganizationTreeResponse.builder()
                .legalEntityId(legalEntityId)
                .legalEntityCode(legalEntityCode)
                .legalEntityName(legalEntityName)
                .status(status)
                .businessUnits(buildBusinessUnitSummaries(businessUnits))
                .branches(buildBranchTrees(branches, locationsByBranch, departmentsByBranch))
                .build();
    }

    private List<BusinessUnitSummaryResponse> buildBusinessUnitSummaries(List<BusinessUnit> businessUnits) {
        List<BusinessUnit> sortedBusinessUnits = new ArrayList<>(businessUnits);
        sortedBusinessUnits.sort(Comparator.comparing(BusinessUnit::getCode));

        List<BusinessUnitSummaryResponse> summaries = new ArrayList<>();
        for (BusinessUnit businessUnit : sortedBusinessUnits) {
            summaries.add(toBusinessUnitSummary(businessUnit));
        }

        return summaries;
    }

    private List<BranchTreeResponse> buildBranchTrees(
            List<Branch> branches,
            Map<UUID, List<Location>> locationsByBranch,
            Map<UUID, List<Department>> departmentsByBranch
    ) {
        List<Branch> sortedBranches = new ArrayList<>(branches);
        sortedBranches.sort(Comparator.comparing(Branch::getCode));

        List<BranchTreeResponse> trees = new ArrayList<>();
        for (Branch branch : sortedBranches) {
            trees.add(toBranchTree(
                    branch,
                    locationsByBranch.getOrDefault(branch.getId(), List.of()),
                    departmentsByBranch.getOrDefault(branch.getId(), List.of())
            ));
        }

        return trees;
    }

    private Map<UUID, List<Location>> groupLocationsByBranch(List<Location> locations) {
        Map<UUID, List<Location>> locationsByBranch = new HashMap<>();

        for (Location location : locations) {
            if (location.getBranchId() == null) {
                continue;
            }

            locationsByBranch
                    .computeIfAbsent(location.getBranchId(), ignored -> new ArrayList<>())
                    .add(location);
        }

        return locationsByBranch;
    }

    private Map<UUID, List<Department>> groupDepartmentsByBranch(List<Department> departments) {
        Map<UUID, List<Department>> departmentsByBranch = new HashMap<>();

        for (Department department : departments) {
            if (department.getBranchId() == null) {
                continue;
            }

            departmentsByBranch
                    .computeIfAbsent(department.getBranchId(), ignored -> new ArrayList<>())
                    .add(department);
        }

        return departmentsByBranch;
    }

    private BusinessUnitSummaryResponse toBusinessUnitSummary(BusinessUnit businessUnit) {
        return BusinessUnitSummaryResponse.builder()
                .id(businessUnit.getId())
                .code(businessUnit.getCode())
                .name(businessUnit.getName())
                .status(businessUnit.getStatus().name())
                .build();
    }

    private BranchTreeResponse toBranchTree(
            Branch branch,
            List<Location> locations,
            List<Department> departments
    ) {
        List<Location> sortedLocations = new ArrayList<>(locations);
        sortedLocations.sort(Comparator.comparing(Location::getCode));

        List<LocationSummaryResponse> locationSummaries = new ArrayList<>();
        for (Location location : sortedLocations) {
            locationSummaries.add(toLocationSummary(location));
        }

        return BranchTreeResponse.builder()
                .id(branch.getId())
                .businessUnitId(branch.getBusinessUnitId())
                .code(branch.getCode())
                .name(branch.getName())
                .status(branch.getStatus().name())
                .locations(locationSummaries)
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

        Map<UUID, Department> departmentsById = new HashMap<>();
        Map<UUID, List<Department>> childrenByParent = new HashMap<>();

        for (Department department : departments) {
            departmentsById.put(department.getId(), department);

            if (department.getParentDepartmentId() != null) {
                childrenByParent
                        .computeIfAbsent(department.getParentDepartmentId(), ignored -> new ArrayList<>())
                        .add(department);
            }
        }

        List<Department> rootDepartments = new ArrayList<>();
        for (Department department : departments) {
            UUID parentDepartmentId = department.getParentDepartmentId();
            if (parentDepartmentId == null || !departmentsById.containsKey(parentDepartmentId)) {
                rootDepartments.add(department);
            }
        }

        rootDepartments.sort(Comparator.comparing(Department::getCode));

        List<DepartmentTreeResponse> tree = new ArrayList<>();
        for (Department department : rootDepartments) {
            tree.add(toDepartmentTree(department, childrenByParent));
        }

        return tree;
    }

    private DepartmentTreeResponse toDepartmentTree(
            Department department,
            Map<UUID, List<Department>> childrenByParent
    ) {
        List<Department> childDepartments = new ArrayList<>(
                childrenByParent.getOrDefault(department.getId(), List.of())
        );
        childDepartments.sort(Comparator.comparing(Department::getCode));

        List<DepartmentTreeResponse> children = new ArrayList<>();
        for (Department childDepartment : childDepartments) {
            children.add(toDepartmentTree(childDepartment, childrenByParent));
        }

        return DepartmentTreeResponse.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .status(department.getStatus().name())
                .children(children)
                .build();
    }
}
