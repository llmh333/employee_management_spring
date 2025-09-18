package com.hit.employee_management_spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.service.ICustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilterChain extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ICustomUserDetailService  customUserDetailService;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractTokenByRequest(request);

        try {
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.extractUsernameByToken(jwt);
                UserPrincipal userPrincipal = customUserDetailService.loadUserByUsername(username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (BadRequestException ex) {
            String message = messageSource.getMessage(ex.getMessage(), null, request.getLocale());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            RestData restData = RestData.error(message);
            response.getOutputStream().write(objectMapper.writeValueAsString(restData).getBytes());
            return;
        } catch (Exception e) {
            String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, request.getLocale());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            RestData restData = RestData.error(message);
            response.getOutputStream().write(objectMapper.writeValueAsString(restData).getBytes());
            e.printStackTrace();
            return;
        }


        filterChain.doFilter(request, response);

    }
    public static String extractTokenByRequest(HttpServletRequest request) {

        String token = null;
        if (request.getHeader("Authorization") != null) {
            token = request.getHeader("Authorization").substring(7);
        }
        return token;
    }


}
