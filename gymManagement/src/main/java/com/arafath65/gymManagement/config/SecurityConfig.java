package com.arafath65.gymManagement.config;

import com.arafath65.gymManagement.security.UserSessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserSessionFilter userSessionFilter;

    public SecurityConfig(UserSessionFilter userSessionFilter) {
        this.userSessionFilter = userSessionFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(userSessionFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Browser preflight requests do not carry the application session.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints required before a user session exists.
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/devices/activate",
                                "/api/devices/verify",
                                "/api/health"
                        ).permitAll()

                        // Every other endpoint requires a valid authenticated session.
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
