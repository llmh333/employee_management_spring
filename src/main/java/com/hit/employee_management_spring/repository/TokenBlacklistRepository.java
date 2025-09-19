package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist,Long> {

    @Transactional
    @Procedure(procedureName = "sp_delete_token_blacklist_expired")
    void deleteExpiredTokenBlacklist();
}
