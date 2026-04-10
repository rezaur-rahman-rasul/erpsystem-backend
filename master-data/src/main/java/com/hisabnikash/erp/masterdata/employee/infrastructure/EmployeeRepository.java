package com.hisabnikash.erp.masterdata.employee.infrastructure;

import com.hisabnikash.erp.masterdata.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    boolean existsByLegalEntityIdAndEmployeeNumberIgnoreCase(UUID legalEntityId, String employeeNumber);

    boolean existsByLegalEntityIdAndEmployeeNumberIgnoreCaseAndIdNot(UUID legalEntityId, String employeeNumber, UUID id);
}
