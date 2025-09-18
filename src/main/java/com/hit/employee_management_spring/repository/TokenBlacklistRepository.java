package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist,Long> {
}
