package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
