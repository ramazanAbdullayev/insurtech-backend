package com.insurtech.backend.config;

import com.insurtech.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "insurtech/v1/auth/login",
                                "insurtech/v1/auth/register",
                                "insurtech/v1/auth/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService::loadSecurityUser;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}
