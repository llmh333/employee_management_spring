package com.hit.employee_management_spring.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
}
