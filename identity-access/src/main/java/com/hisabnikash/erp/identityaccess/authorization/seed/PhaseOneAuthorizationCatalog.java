package com.hisabnikash.erp.identityaccess.authorization.seed;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResourceType;
import com.hisabnikash.erp.identityaccess.security.permission.PhaseOnePermissions;

import java.util.ArrayList;
import java.util.List;

public final class PhaseOneAuthorizationCatalog {

    private PhaseOneAuthorizationCatalog() {
    }

    public static List<ActionSeedDefinition> actions() {
        return List.of(
                new ActionSeedDefinition("VIEW", "View", "SERVICE,MODULE,SCREEN,API_GROUP,API,BUTTON,FIELD,SECTION,TAB"),
                new ActionSeedDefinition("CREATE", "Create", "SCREEN,API"),
                new ActionSeedDefinition("EDIT", "Edit", "SCREEN,FIELD,SECTION,TAB,API"),
                new ActionSeedDefinition("DELETE", "Delete", "SCREEN,API"),
                new ActionSeedDefinition("APPROVE", "Approve", "SCREEN,API,BUTTON"),
                new ActionSeedDefinition("REJECT", "Reject", "SCREEN,API,BUTTON"),
                new ActionSeedDefinition("EXPORT", "Export", "SCREEN,API,BUTTON"),
                new ActionSeedDefinition("PRINT", "Print", "SCREEN,API,BUTTON"),
                new ActionSeedDefinition("EXECUTE", "Execute", "BUTTON,API")
        );
    }

    public static List<PermissionSeedDefinition> permissions() {
        List<PermissionSeedDefinition> definitions = new ArrayList<>();

        addCrud(definitions, "IAM", "Identity and Access", "USER", "User Management",
                PhaseOnePermissions.IDENTITY_USER_VIEW, PhaseOnePermissions.IDENTITY_USER_CREATE, PhaseOnePermissions.IDENTITY_USER_UPDATE,
                "User List", "User API", "View user accounts", "Create user accounts", "Update user accounts");
        addCrud(definitions, "IAM", "Identity and Access", "ROLE", "Role Management",
                PhaseOnePermissions.IDENTITY_ROLE_VIEW, PhaseOnePermissions.IDENTITY_ROLE_CREATE, PhaseOnePermissions.IDENTITY_ROLE_UPDATE,
                "Role List", "Role API", "View roles", "Create roles", "Update roles");
        addViewOnly(definitions, "IAM", "Identity and Access", "PERM", "Permission Catalog",
                PhaseOnePermissions.IDENTITY_PERMISSION_VIEW, "CATALOG", "Permission Catalog",
                "View permission catalog");
        addCrud(definitions, "IAM", "Identity and Access", "ACCESS", "Organization Access",
                PhaseOnePermissions.IDENTITY_ACCESS_VIEW, PhaseOnePermissions.IDENTITY_ACCESS_CREATE, PhaseOnePermissions.IDENTITY_ACCESS_UPDATE,
                "Organization Access List", "Organization Access API",
                "View organization access assignments", "Create organization access assignments", "Update organization access assignments");

        addCrud(definitions, "MDM", "Master Data", "CUSTOMER", "Customer Management",
                PhaseOnePermissions.MASTER_DATA_CUSTOMER_VIEW, PhaseOnePermissions.MASTER_DATA_CUSTOMER_CREATE, PhaseOnePermissions.MASTER_DATA_CUSTOMER_UPDATE,
                "Customer List", "Customer API", "View customers", "Create customers", "Update customers");
        addCrud(definitions, "MDM", "Master Data", "SUPPLIER", "Supplier Management",
                PhaseOnePermissions.MASTER_DATA_SUPPLIER_VIEW, PhaseOnePermissions.MASTER_DATA_SUPPLIER_CREATE, PhaseOnePermissions.MASTER_DATA_SUPPLIER_UPDATE,
                "Supplier List", "Supplier API", "View suppliers", "Create suppliers", "Update suppliers");
        addCrud(definitions, "MDM", "Master Data", "WAREHOUSE", "Warehouse Management",
                PhaseOnePermissions.MASTER_DATA_WAREHOUSE_VIEW, PhaseOnePermissions.MASTER_DATA_WAREHOUSE_CREATE, PhaseOnePermissions.MASTER_DATA_WAREHOUSE_UPDATE,
                "Warehouse List", "Warehouse API", "View warehouses", "Create warehouses", "Update warehouses");
        addCrud(definitions, "MDM", "Master Data", "TAX_CODE", "Tax Code Management",
                PhaseOnePermissions.MASTER_DATA_TAX_CODE_VIEW, PhaseOnePermissions.MASTER_DATA_TAX_CODE_CREATE, PhaseOnePermissions.MASTER_DATA_TAX_CODE_UPDATE,
                "Tax Code List", "Tax Code API", "View tax codes", "Create tax codes", "Update tax codes");
        addCrud(definitions, "MDM", "Master Data", "PRODUCT", "Product Management",
                PhaseOnePermissions.MASTER_DATA_PRODUCT_VIEW, PhaseOnePermissions.MASTER_DATA_PRODUCT_CREATE, PhaseOnePermissions.MASTER_DATA_PRODUCT_UPDATE,
                "Product List", "Product API", "View products", "Create products", "Update products");
        addCrud(definitions, "MDM", "Master Data", "EMPLOYEE", "Employee Management",
                PhaseOnePermissions.MASTER_DATA_EMPLOYEE_VIEW, PhaseOnePermissions.MASTER_DATA_EMPLOYEE_CREATE, PhaseOnePermissions.MASTER_DATA_EMPLOYEE_UPDATE,
                "Employee List", "Employee API", "View employees", "Create employees", "Update employees");
        addCrud(definitions, "MDM", "Master Data", "CHART_OF_ACCOUNT", "Chart of Account Management",
                PhaseOnePermissions.MASTER_DATA_CHART_OF_ACCOUNT_VIEW, PhaseOnePermissions.MASTER_DATA_CHART_OF_ACCOUNT_CREATE, PhaseOnePermissions.MASTER_DATA_CHART_OF_ACCOUNT_UPDATE,
                "Chart of Account List", "Chart of Account API", "View chart of accounts", "Create chart of accounts", "Update chart of accounts");
        addCrud(definitions, "MDM", "Master Data", "CURRENCY", "Currency Management",
                PhaseOnePermissions.MASTER_DATA_CURRENCY_VIEW, PhaseOnePermissions.MASTER_DATA_CURRENCY_CREATE, PhaseOnePermissions.MASTER_DATA_CURRENCY_UPDATE,
                "Currency List", "Currency API", "View currencies", "Create currencies", "Update currencies");
        addCrud(definitions, "MDM", "Master Data", "UOM", "Unit of Measure Management",
                PhaseOnePermissions.MASTER_DATA_UOM_VIEW, PhaseOnePermissions.MASTER_DATA_UOM_CREATE, PhaseOnePermissions.MASTER_DATA_UOM_UPDATE,
                "Unit of Measure List", "Unit of Measure API", "View units of measure", "Create units of measure", "Update units of measure");
        addCrud(definitions, "MDM", "Master Data", "PAYMENT_TERM", "Payment Term Management",
                PhaseOnePermissions.MASTER_DATA_PAYMENT_TERM_VIEW, PhaseOnePermissions.MASTER_DATA_PAYMENT_TERM_CREATE, PhaseOnePermissions.MASTER_DATA_PAYMENT_TERM_UPDATE,
                "Payment Term List", "Payment Term API", "View payment terms", "Create payment terms", "Update payment terms");

        addCrud(definitions, "ORG", "Organization", "LEGAL_ENTITY", "Legal Entity Management",
                PhaseOnePermissions.ENTERPRISE_LEGAL_ENTITY_VIEW, PhaseOnePermissions.ENTERPRISE_LEGAL_ENTITY_CREATE, PhaseOnePermissions.ENTERPRISE_LEGAL_ENTITY_UPDATE,
                "Legal Entity List", "Legal Entity API", "View legal entities", "Create legal entities", "Update legal entities");
        addCrud(definitions, "ORG", "Organization", "BUSINESS_UNIT", "Business Unit Management",
                PhaseOnePermissions.ENTERPRISE_BUSINESS_UNIT_VIEW, PhaseOnePermissions.ENTERPRISE_BUSINESS_UNIT_CREATE, PhaseOnePermissions.ENTERPRISE_BUSINESS_UNIT_UPDATE,
                "Business Unit List", "Business Unit API", "View business units", "Create business units", "Update business units");
        addCrud(definitions, "ORG", "Organization", "BRANCH", "Branch Management",
                PhaseOnePermissions.ENTERPRISE_BRANCH_VIEW, PhaseOnePermissions.ENTERPRISE_BRANCH_CREATE, PhaseOnePermissions.ENTERPRISE_BRANCH_UPDATE,
                "Branch List", "Branch API", "View branches", "Create branches", "Update branches");
        addCrud(definitions, "ORG", "Organization", "DEPARTMENT", "Department Management",
                PhaseOnePermissions.ENTERPRISE_DEPARTMENT_VIEW, PhaseOnePermissions.ENTERPRISE_DEPARTMENT_CREATE, PhaseOnePermissions.ENTERPRISE_DEPARTMENT_UPDATE,
                "Department List", "Department API", "View departments", "Create departments", "Update departments");
        addCrud(definitions, "ORG", "Organization", "COST_CENTER", "Cost Center Management",
                PhaseOnePermissions.ENTERPRISE_COST_CENTER_VIEW, PhaseOnePermissions.ENTERPRISE_COST_CENTER_CREATE, PhaseOnePermissions.ENTERPRISE_COST_CENTER_UPDATE,
                "Cost Center List", "Cost Center API", "View cost centers", "Create cost centers", "Update cost centers");
        addCrud(definitions, "ORG", "Organization", "PROFIT_CENTER", "Profit Center Management",
                PhaseOnePermissions.ENTERPRISE_PROFIT_CENTER_VIEW, PhaseOnePermissions.ENTERPRISE_PROFIT_CENTER_CREATE, PhaseOnePermissions.ENTERPRISE_PROFIT_CENTER_UPDATE,
                "Profit Center List", "Profit Center API", "View profit centers", "Create profit centers", "Update profit centers");
        addCrud(definitions, "ORG", "Organization", "SUBSIDIARY", "Subsidiary Management",
                PhaseOnePermissions.ENTERPRISE_SUBSIDIARY_VIEW, PhaseOnePermissions.ENTERPRISE_SUBSIDIARY_CREATE, PhaseOnePermissions.ENTERPRISE_SUBSIDIARY_UPDATE,
                "Subsidiary List", "Subsidiary API", "View subsidiaries", "Create subsidiaries", "Update subsidiaries");
        addCrud(definitions, "ORG", "Organization", "FISCAL_CALENDAR", "Fiscal Calendar Management",
                PhaseOnePermissions.ENTERPRISE_FISCAL_CALENDAR_VIEW, PhaseOnePermissions.ENTERPRISE_FISCAL_CALENDAR_CREATE, PhaseOnePermissions.ENTERPRISE_FISCAL_CALENDAR_UPDATE,
                "Fiscal Calendar List", "Fiscal Calendar API", "View fiscal calendars", "Create fiscal calendars", "Update fiscal calendars");
        addCrud(definitions, "ORG", "Organization", "TENANT_PROFILE", "Tenant Profile Management",
                PhaseOnePermissions.ENTERPRISE_TENANT_PROFILE_VIEW, PhaseOnePermissions.ENTERPRISE_TENANT_PROFILE_CREATE, PhaseOnePermissions.ENTERPRISE_TENANT_PROFILE_UPDATE,
                "Tenant Profile List", "Tenant Profile API", "View tenant profiles", "Create tenant profiles", "Update tenant profiles");
        addCrud(definitions, "ORG", "Organization", "LOCATION", "Location Management",
                PhaseOnePermissions.ENTERPRISE_LOCATION_VIEW, PhaseOnePermissions.ENTERPRISE_LOCATION_CREATE, PhaseOnePermissions.ENTERPRISE_LOCATION_UPDATE,
                "Location List", "Location API", "View locations", "Create locations", "Update locations");
        addViewOnly(definitions, "ORG", "Organization", "HIERARCHY", "Organization Hierarchy",
                PhaseOnePermissions.ENTERPRISE_HIERARCHY_VIEW, "TREE", "Organization Hierarchy",
                "View organization hierarchy");
        addCrud(definitions, "ORG", "Organization", "SETTINGS", "Organization Settings",
                PhaseOnePermissions.ENTERPRISE_SETTINGS_VIEW, PhaseOnePermissions.ENTERPRISE_SETTINGS_CREATE, PhaseOnePermissions.ENTERPRISE_SETTINGS_UPDATE,
                "Settings Screen", "Settings API", "View organization settings", "Create organization settings", "Update organization settings");

        return List.copyOf(definitions);
    }

    private static void addCrud(List<PermissionSeedDefinition> definitions,
                                String serviceCode,
                                String serviceName,
                                String moduleCode,
                                String moduleName,
                                String viewLegacyCode,
                                String createLegacyCode,
                                String updateLegacyCode,
                                String listResourceName,
                                String apiResourceName,
                                String viewDescription,
                                String createDescription,
                                String updateDescription) {
        definitions.add(new PermissionSeedDefinition(
                viewLegacyCode,
                serviceCode,
                serviceName,
                moduleCode,
                moduleName,
                "LIST",
                AuthorizationResourceType.SCREEN,
                listResourceName,
                "VIEW",
                viewDescription
        ));
        definitions.add(new PermissionSeedDefinition(
                createLegacyCode,
                serviceCode,
                serviceName,
                moduleCode,
                moduleName,
                "API.SAVE",
                AuthorizationResourceType.API,
                apiResourceName,
                "CREATE",
                createDescription
        ));
        definitions.add(new PermissionSeedDefinition(
                updateLegacyCode,
                serviceCode,
                serviceName,
                moduleCode,
                moduleName,
                "API.SAVE",
                AuthorizationResourceType.API,
                apiResourceName,
                "EDIT",
                updateDescription
        ));
    }

    private static void addViewOnly(List<PermissionSeedDefinition> definitions,
                                    String serviceCode,
                                    String serviceName,
                                    String moduleCode,
                                    String moduleName,
                                    String legacyCode,
                                    String resourcePath,
                                    String resourceName,
                                    String description) {
        definitions.add(new PermissionSeedDefinition(
                legacyCode,
                serviceCode,
                serviceName,
                moduleCode,
                moduleName,
                resourcePath,
                AuthorizationResourceType.SCREEN,
                resourceName,
                "VIEW",
                description
        ));
    }
}
