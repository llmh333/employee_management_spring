package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    UserSession findUserSessionByAccessToken(String accessToken);

    List<UserSession> findAllByUser(User user);
}
