package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.security.UserPrincipal;

public interface ICustomUserDetailService {

    public UserPrincipal loadUserByUsername(String username);

    public UserPrincipal loadUserByEmail(String email);
}
