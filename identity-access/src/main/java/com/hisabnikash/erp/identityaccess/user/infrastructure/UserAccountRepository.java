package com.hisabnikash.erp.identityaccess.user.infrastructure;

import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, UUID id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    Optional<UserAccount> findByEmailIgnoreCase(String email);
}
