package com.hisabnikash.erp.identityaccess.security.permission;

import com.hisabnikash.erp.identityaccess.permission.dto.PermissionDefinitionResponse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PhaseOnePermissions {

    public static final String IDENTITY_USER_VIEW = "identity:user:view";
    public static final String IDENTITY_USER_CREATE = "identity:user:create";
    public static final String IDENTITY_USER_UPDATE = "identity:user:update";
    public static final String IDENTITY_ROLE_VIEW = "identity:role:view";
    public static final String IDENTITY_ROLE_CREATE = "identity:role:create";
    public static final String IDENTITY_ROLE_UPDATE = "identity:role:update";
    public static final String IDENTITY_PERMISSION_VIEW = "identity:permission:view";
    public static final String IDENTITY_ACCESS_VIEW = "identity:access:view";
    public static final String IDENTITY_ACCESS_CREATE = "identity:access:create";
    public static final String IDENTITY_ACCESS_UPDATE = "identity:access:update";

    public static final String MASTER_DATA_CUSTOMER_VIEW = "master-data:customer:view";
    public static final String MASTER_DATA_CUSTOMER_CREATE = "master-data:customer:create";
    public static final String MASTER_DATA_CUSTOMER_UPDATE = "master-data:customer:update";
    public static final String MASTER_DATA_SUPPLIER_VIEW = "master-data:supplier:view";
    public static final String MASTER_DATA_SUPPLIER_CREATE = "master-data:supplier:create";
    public static final String MASTER_DATA_SUPPLIER_UPDATE = "master-data:supplier:update";
    public static final String MASTER_DATA_WAREHOUSE_VIEW = "master-data:warehouse:view";
    public static final String MASTER_DATA_WAREHOUSE_CREATE = "master-data:warehouse:create";
    public static final String MASTER_DATA_WAREHOUSE_UPDATE = "master-data:warehouse:update";
    public static final String MASTER_DATA_TAX_CODE_VIEW = "master-data:tax-code:view";
    public static final String MASTER_DATA_TAX_CODE_CREATE = "master-data:tax-code:create";
    public static final String MASTER_DATA_TAX_CODE_UPDATE = "master-data:tax-code:update";
    public static final String MASTER_DATA_PRODUCT_VIEW = "master-data:product:view";
    public static final String MASTER_DATA_PRODUCT_CREATE = "master-data:product:create";
    public static final String MASTER_DATA_PRODUCT_UPDATE = "master-data:product:update";
    public static final String MASTER_DATA_EMPLOYEE_VIEW = "master-data:employee:view";
    public static final String MASTER_DATA_EMPLOYEE_CREATE = "master-data:employee:create";
    public static final String MASTER_DATA_EMPLOYEE_UPDATE = "master-data:employee:update";
    public static final String MASTER_DATA_CHART_OF_ACCOUNT_VIEW = "master-data:chart-of-account:view";
    public static final String MASTER_DATA_CHART_OF_ACCOUNT_CREATE = "master-data:chart-of-account:create";
    public static final String MASTER_DATA_CHART_OF_ACCOUNT_UPDATE = "master-data:chart-of-account:update";
    public static final String MASTER_DATA_CURRENCY_VIEW = "master-data:currency:view";
    public static final String MASTER_DATA_CURRENCY_CREATE = "master-data:currency:create";
    public static final String MASTER_DATA_CURRENCY_UPDATE = "master-data:currency:update";
    public static final String MASTER_DATA_UOM_VIEW = "master-data:uom:view";
    public static final String MASTER_DATA_UOM_CREATE = "master-data:uom:create";
    public static final String MASTER_DATA_UOM_UPDATE = "master-data:uom:update";
    public static final String MASTER_DATA_PAYMENT_TERM_VIEW = "master-data:payment-term:view";
    public static final String MASTER_DATA_PAYMENT_TERM_CREATE = "master-data:payment-term:create";
    public static final String MASTER_DATA_PAYMENT_TERM_UPDATE = "master-data:payment-term:update";

    public static final String ENTERPRISE_LEGAL_ENTITY_CREATE = "enterprise:legal-entity:create";
    public static final String ENTERPRISE_LEGAL_ENTITY_UPDATE = "enterprise:legal-entity:update";
    public static final String ENTERPRISE_LEGAL_ENTITY_VIEW = "enterprise:legal-entity:view";
    public static final String ENTERPRISE_BUSINESS_UNIT_CREATE = "enterprise:business-unit:create";
    public static final String ENTERPRISE_BUSINESS_UNIT_UPDATE = "enterprise:business-unit:update";
    public static final String ENTERPRISE_BUSINESS_UNIT_VIEW = "enterprise:business-unit:view";
    public static final String ENTERPRISE_BRANCH_CREATE = "enterprise:branch:create";
    public static final String ENTERPRISE_BRANCH_UPDATE = "enterprise:branch:update";
    public static final String ENTERPRISE_BRANCH_VIEW = "enterprise:branch:view";
    public static final String ENTERPRISE_DEPARTMENT_CREATE = "enterprise:department:create";
    public static final String ENTERPRISE_DEPARTMENT_UPDATE = "enterprise:department:update";
    public static final String ENTERPRISE_DEPARTMENT_VIEW = "enterprise:department:view";
    public static final String ENTERPRISE_COST_CENTER_CREATE = "enterprise:cost-center:create";
    public static final String ENTERPRISE_COST_CENTER_UPDATE = "enterprise:cost-center:update";
    public static final String ENTERPRISE_COST_CENTER_VIEW = "enterprise:cost-center:view";
    public static final String ENTERPRISE_PROFIT_CENTER_CREATE = "enterprise:profit-center:create";
    public static final String ENTERPRISE_PROFIT_CENTER_UPDATE = "enterprise:profit-center:update";
    public static final String ENTERPRISE_PROFIT_CENTER_VIEW = "enterprise:profit-center:view";
    public static final String ENTERPRISE_SUBSIDIARY_CREATE = "enterprise:subsidiary:create";
    public static final String ENTERPRISE_SUBSIDIARY_UPDATE = "enterprise:subsidiary:update";
    public static final String ENTERPRISE_SUBSIDIARY_VIEW = "enterprise:subsidiary:view";
    public static final String ENTERPRISE_FISCAL_CALENDAR_CREATE = "enterprise:fiscal-calendar:create";
    public static final String ENTERPRISE_FISCAL_CALENDAR_UPDATE = "enterprise:fiscal-calendar:update";
    public static final String ENTERPRISE_FISCAL_CALENDAR_VIEW = "enterprise:fiscal-calendar:view";
    public static final String ENTERPRISE_TENANT_PROFILE_CREATE = "enterprise:tenant-profile:create";
    public static final String ENTERPRISE_TENANT_PROFILE_UPDATE = "enterprise:tenant-profile:update";
    public static final String ENTERPRISE_TENANT_PROFILE_VIEW = "enterprise:tenant-profile:view";
    public static final String ENTERPRISE_LOCATION_CREATE = "enterprise:location:create";
    public static final String ENTERPRISE_LOCATION_UPDATE = "enterprise:location:update";
    public static final String ENTERPRISE_LOCATION_VIEW = "enterprise:location:view";
    public static final String ENTERPRISE_HIERARCHY_VIEW = "enterprise:hierarchy:view";
    public static final String ENTERPRISE_SETTINGS_CREATE = "enterprise:settings:create";
    public static final String ENTERPRISE_SETTINGS_UPDATE = "enterprise:settings:update";
    public static final String ENTERPRISE_SETTINGS_VIEW = "enterprise:settings:view";

    private PhaseOnePermissions() {
    }

    public static List<PermissionDefinitionResponse> catalog() {
        return List.of(
                new PermissionDefinitionResponse(IDENTITY_USER_VIEW, "identity-access", "View user accounts"),
                new PermissionDefinitionResponse(IDENTITY_USER_CREATE, "identity-access", "Create user accounts"),
                new PermissionDefinitionResponse(IDENTITY_USER_UPDATE, "identity-access", "Update user accounts"),
                new PermissionDefinitionResponse(IDENTITY_ROLE_VIEW, "identity-access", "View roles"),
                new PermissionDefinitionResponse(IDENTITY_ROLE_CREATE, "identity-access", "Create roles"),
                new PermissionDefinitionResponse(IDENTITY_ROLE_UPDATE, "identity-access", "Update roles"),
                new PermissionDefinitionResponse(IDENTITY_PERMISSION_VIEW, "identity-access", "View permission catalog"),
                new PermissionDefinitionResponse(IDENTITY_ACCESS_VIEW, "identity-access", "View organization access assignments"),
                new PermissionDefinitionResponse(IDENTITY_ACCESS_CREATE, "identity-access", "Create organization access assignments"),
                new PermissionDefinitionResponse(IDENTITY_ACCESS_UPDATE, "identity-access", "Update organization access assignments"),
                new PermissionDefinitionResponse(MASTER_DATA_CUSTOMER_VIEW, "master-data", "View customers"),
                new PermissionDefinitionResponse(MASTER_DATA_CUSTOMER_CREATE, "master-data", "Create customers"),
                new PermissionDefinitionResponse(MASTER_DATA_CUSTOMER_UPDATE, "master-data", "Update customers"),
                new PermissionDefinitionResponse(MASTER_DATA_SUPPLIER_VIEW, "master-data", "View suppliers"),
                new PermissionDefinitionResponse(MASTER_DATA_SUPPLIER_CREATE, "master-data", "Create suppliers"),
                new PermissionDefinitionResponse(MASTER_DATA_SUPPLIER_UPDATE, "master-data", "Update suppliers"),
                new PermissionDefinitionResponse(MASTER_DATA_WAREHOUSE_VIEW, "master-data", "View warehouses"),
                new PermissionDefinitionResponse(MASTER_DATA_WAREHOUSE_CREATE, "master-data", "Create warehouses"),
                new PermissionDefinitionResponse(MASTER_DATA_WAREHOUSE_UPDATE, "master-data", "Update warehouses"),
                new PermissionDefinitionResponse(MASTER_DATA_TAX_CODE_VIEW, "master-data", "View tax codes"),
                new PermissionDefinitionResponse(MASTER_DATA_TAX_CODE_CREATE, "master-data", "Create tax codes"),
                new PermissionDefinitionResponse(MASTER_DATA_TAX_CODE_UPDATE, "master-data", "Update tax codes"),
                new PermissionDefinitionResponse(MASTER_DATA_PRODUCT_VIEW, "master-data", "View products"),
                new PermissionDefinitionResponse(MASTER_DATA_PRODUCT_CREATE, "master-data", "Create products"),
                new PermissionDefinitionResponse(MASTER_DATA_PRODUCT_UPDATE, "master-data", "Update products"),
                new PermissionDefinitionResponse(MASTER_DATA_EMPLOYEE_VIEW, "master-data", "View employees"),
                new PermissionDefinitionResponse(MASTER_DATA_EMPLOYEE_CREATE, "master-data", "Create employees"),
                new PermissionDefinitionResponse(MASTER_DATA_EMPLOYEE_UPDATE, "master-data", "Update employees"),
                new PermissionDefinitionResponse(MASTER_DATA_CHART_OF_ACCOUNT_VIEW, "master-data", "View chart of accounts"),
                new PermissionDefinitionResponse(MASTER_DATA_CHART_OF_ACCOUNT_CREATE, "master-data", "Create chart of accounts"),
                new PermissionDefinitionResponse(MASTER_DATA_CHART_OF_ACCOUNT_UPDATE, "master-data", "Update chart of accounts"),
                new PermissionDefinitionResponse(MASTER_DATA_CURRENCY_VIEW, "master-data", "View currencies"),
                new PermissionDefinitionResponse(MASTER_DATA_CURRENCY_CREATE, "master-data", "Create currencies"),
                new PermissionDefinitionResponse(MASTER_DATA_CURRENCY_UPDATE, "master-data", "Update currencies"),
                new PermissionDefinitionResponse(MASTER_DATA_UOM_VIEW, "master-data", "View units of measure"),
                new PermissionDefinitionResponse(MASTER_DATA_UOM_CREATE, "master-data", "Create units of measure"),
                new PermissionDefinitionResponse(MASTER_DATA_UOM_UPDATE, "master-data", "Update units of measure"),
                new PermissionDefinitionResponse(MASTER_DATA_PAYMENT_TERM_VIEW, "master-data", "View payment terms"),
                new PermissionDefinitionResponse(MASTER_DATA_PAYMENT_TERM_CREATE, "master-data", "Create payment terms"),
                new PermissionDefinitionResponse(MASTER_DATA_PAYMENT_TERM_UPDATE, "master-data", "Update payment terms"),
                new PermissionDefinitionResponse(ENTERPRISE_LEGAL_ENTITY_CREATE, "organization", "Create legal entities"),
                new PermissionDefinitionResponse(ENTERPRISE_LEGAL_ENTITY_UPDATE, "organization", "Update legal entities"),
                new PermissionDefinitionResponse(ENTERPRISE_LEGAL_ENTITY_VIEW, "organization", "View legal entities"),
                new PermissionDefinitionResponse(ENTERPRISE_BUSINESS_UNIT_CREATE, "organization", "Create business units"),
                new PermissionDefinitionResponse(ENTERPRISE_BUSINESS_UNIT_UPDATE, "organization", "Update business units"),
                new PermissionDefinitionResponse(ENTERPRISE_BUSINESS_UNIT_VIEW, "organization", "View business units"),
                new PermissionDefinitionResponse(ENTERPRISE_BRANCH_CREATE, "organization", "Create branches"),
                new PermissionDefinitionResponse(ENTERPRISE_BRANCH_UPDATE, "organization", "Update branches"),
                new PermissionDefinitionResponse(ENTERPRISE_BRANCH_VIEW, "organization", "View branches"),
                new PermissionDefinitionResponse(ENTERPRISE_DEPARTMENT_CREATE, "organization", "Create departments"),
                new PermissionDefinitionResponse(ENTERPRISE_DEPARTMENT_UPDATE, "organization", "Update departments"),
                new PermissionDefinitionResponse(ENTERPRISE_DEPARTMENT_VIEW, "organization", "View departments"),
                new PermissionDefinitionResponse(ENTERPRISE_COST_CENTER_CREATE, "organization", "Create cost centers"),
                new PermissionDefinitionResponse(ENTERPRISE_COST_CENTER_UPDATE, "organization", "Update cost centers"),
                new PermissionDefinitionResponse(ENTERPRISE_COST_CENTER_VIEW, "organization", "View cost centers"),
                new PermissionDefinitionResponse(ENTERPRISE_PROFIT_CENTER_CREATE, "organization", "Create profit centers"),
                new PermissionDefinitionResponse(ENTERPRISE_PROFIT_CENTER_UPDATE, "organization", "Update profit centers"),
                new PermissionDefinitionResponse(ENTERPRISE_PROFIT_CENTER_VIEW, "organization", "View profit centers"),
                new PermissionDefinitionResponse(ENTERPRISE_SUBSIDIARY_CREATE, "organization", "Create subsidiaries"),
                new PermissionDefinitionResponse(ENTERPRISE_SUBSIDIARY_UPDATE, "organization", "Update subsidiaries"),
                new PermissionDefinitionResponse(ENTERPRISE_SUBSIDIARY_VIEW, "organization", "View subsidiaries"),
                new PermissionDefinitionResponse(ENTERPRISE_FISCAL_CALENDAR_CREATE, "organization", "Create fiscal calendars"),
                new PermissionDefinitionResponse(ENTERPRISE_FISCAL_CALENDAR_UPDATE, "organization", "Update fiscal calendars"),
                new PermissionDefinitionResponse(ENTERPRISE_FISCAL_CALENDAR_VIEW, "organization", "View fiscal calendars"),
                new PermissionDefinitionResponse(ENTERPRISE_TENANT_PROFILE_CREATE, "organization", "Create tenant profiles"),
                new PermissionDefinitionResponse(ENTERPRISE_TENANT_PROFILE_UPDATE, "organization", "Update tenant profiles"),
                new PermissionDefinitionResponse(ENTERPRISE_TENANT_PROFILE_VIEW, "organization", "View tenant profiles"),
                new PermissionDefinitionResponse(ENTERPRISE_LOCATION_CREATE, "organization", "Create locations"),
                new PermissionDefinitionResponse(ENTERPRISE_LOCATION_UPDATE, "organization", "Update locations"),
                new PermissionDefinitionResponse(ENTERPRISE_LOCATION_VIEW, "organization", "View locations"),
                new PermissionDefinitionResponse(ENTERPRISE_HIERARCHY_VIEW, "organization", "View organization hierarchy"),
                new PermissionDefinitionResponse(ENTERPRISE_SETTINGS_CREATE, "organization", "Create organization settings"),
                new PermissionDefinitionResponse(ENTERPRISE_SETTINGS_UPDATE, "organization", "Update organization settings"),
                new PermissionDefinitionResponse(ENTERPRISE_SETTINGS_VIEW, "organization", "View organization settings")
        );
    }

    public static Set<String> all() {
        return catalog().stream()
                .map(PermissionDefinitionResponse::code)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }
}
