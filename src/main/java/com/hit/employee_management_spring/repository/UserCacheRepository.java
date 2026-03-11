package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.User;

public interface UserCacheRepository {

    boolean save(User user);
    User get(String username);
    boolean delete(String username);
}
