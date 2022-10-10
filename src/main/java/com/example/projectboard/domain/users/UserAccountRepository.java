package com.example.projectboard.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>,
        UserAccountRepositoryCustom{

    Optional<UserAccount> findByUsername(String username);
}
