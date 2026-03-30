package com.insurtech.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;

@Configuration
public class CustomJwtAuthenticationConverter {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(this::extractRoles);

        converter.setPrincipalClaimName("sub");

        return converter;
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Object rawRoles = jwt.getClaim("roles");
        if (rawRoles instanceof List<?> roles) {
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(r -> (GrantedAuthority) new SimpleGrantedAuthority((String) r))
                    .toList();
        }
        return List.of();
    }
}
