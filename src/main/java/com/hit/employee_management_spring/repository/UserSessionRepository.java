package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    UserSession findUserSessionByAccessToken(String accessToken);

    @Transactional
    @Query(value = "CALL sp_delete_token_blacklist_expired()", nativeQuery = true)
    void deleteUSerSessionExpired();
}
