package com.arafath65.gymManagement.security;

import com.arafath65.gymManagement.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class UserSessionFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-User-Session";

    private final UserSessionService sessionService;

    public UserSessionFilter(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // These endpoints must be accessible before login or without an existing session.
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/logout")
                || path.equals("/api/devices/activate")
                || path.equals("/api/devices/verify")
                || path.equals("/api/health");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = request.getHeader(HEADER);

        // Fail closed: all non-public endpoints require a session token.
        if (token == null || token.isBlank()) {
            writeUnauthorized(response, "A valid user session is required. Please log in.");
            return;
        }

        Optional<AuthenticatedUser> authenticated = sessionService.authenticate(token);

        if (authenticated.isEmpty()) {
            writeUnauthorized(response, "Session is invalid, expired or revoked. Please log in again.");
            return;
        }

        AuthenticatedUser principal = authenticated.get();

        List<SimpleGrantedAuthority> authorities = principal.roleName() == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority(
                "ROLE_" + principal.roleName().toUpperCase().replace(' ', '_')
        ));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, token, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + message + "\"}"
        );
    }
}
