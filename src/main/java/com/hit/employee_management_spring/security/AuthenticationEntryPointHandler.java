package com.hit.employee_management_spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, request.getLocale());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        RestData restData = RestData.error(message);
        response.getOutputStream().write(objectMapper.writeValueAsString(restData).getBytes());
    }
}
