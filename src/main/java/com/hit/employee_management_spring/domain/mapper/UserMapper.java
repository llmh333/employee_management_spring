package com.hit.employee_management_spring.domain.mapper;

import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterUserRequestDto registerUserRequestDto);

    UserResponseDto toUserResponseDto(User user);
}
