package com.insurtech.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
public class JwtDecoderConfig {

    @Value("${spring.auth.jwt.issuer}")
    private String issuer;

    @Value("${spring.auth.jwt.audience}")
    private String audience;

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(rsaPublicKey)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>(
                        JwtClaimNames.AUD,
                        aud -> aud != null && aud.contains(audience));

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

        return decoder;
    }
}
