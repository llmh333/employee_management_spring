package com.hit.employee_management_spring.security;

import com.hit.employee_management_spring.service.ICustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractTokenByRequest(request);

        try {
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.extractUsernameByToken(jwt);
                UserPrincipal userPrincipal = customUserDetailService.loadUserByUsername(username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        filterChain.doFilter(request, response);

    }
    private String extractTokenByRequest(HttpServletRequest request) {

        String token = null;
        if (request.getHeader("Authorization") != null) {
            token = request.getHeader("Authorization").substring(7);
        }
        return token;
    }


}
