package com.example.familyplanner.Security;

import com.example.familyplanner.Security.JWT.JwtCore;
import com.example.familyplanner.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final UserService userService;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/sign-up",
            "/api/auth/sign-in",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/verify-reset-code",
            "/api/auth",
            "/swagger-ui/",
            "/v3/api-docs/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true; // Always allow pre-flight CORS requests
        }

        // Skip filtering if the path matches any public endpoint
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = parseJwt(request);

        if (jwt != null && jwtCore.validateToken(jwt)) {
            String email = jwtCore.getUserNameFromJwt(jwt);
            UserDetails userDetails = userService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
