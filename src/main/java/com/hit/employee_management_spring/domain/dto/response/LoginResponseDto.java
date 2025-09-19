package com.hit.employee_management_spring.domain.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String typeToken = "Bearer";
    private String accessToken;
    private String refreshToken;
    private String username;

    public LoginResponseDto(String accessToken, String refreshToken, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
    }
}
