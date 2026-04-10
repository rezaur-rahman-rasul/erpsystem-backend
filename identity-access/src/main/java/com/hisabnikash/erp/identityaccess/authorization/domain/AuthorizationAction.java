package com.hisabnikash.erp.identityaccess.authorization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auth_actions")
@Getter
@Setter
@NoArgsConstructor
public class AuthorizationAction {

    @Id
    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "applies_to_types", nullable = false, length = 200)
    private String appliesToTypes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuthorizationStatus status = AuthorizationStatus.ACTIVE;
}
