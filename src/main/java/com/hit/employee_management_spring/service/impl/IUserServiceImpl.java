package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;
}
