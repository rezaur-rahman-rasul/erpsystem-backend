package com.hisabnikash.erp.identityaccess.role.domain;

import com.hisabnikash.erp.identityaccess.authorization.domain.RolePermissionGrant;
import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_code", nullable = false, length = 120)
    private Set<String> legacyPermissions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermissionGrant> permissionGrants = new LinkedHashSet<>();

    public Set<String> getPermissions() {
        return new LinkedHashSet<>(legacyPermissions);
    }

    public void setPermissions(Set<String> permissions) {
        legacyPermissions = normalizePermissions(permissions);
    }

    public void replacePermissionGrants(Collection<RolePermissionGrant> grants) {
        permissionGrants.clear();
        if (grants != null) {
            permissionGrants.addAll(grants);
        }
    }

    private Set<String> normalizePermissions(Set<String> permissions) {
        if (permissions == null) {
            return new LinkedHashSet<>();
        }
        return permissions.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }
}
