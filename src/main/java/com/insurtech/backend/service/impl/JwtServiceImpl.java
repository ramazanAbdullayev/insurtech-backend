package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.exception.AuthException;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey pubicKey;
    private final String issuer;
    private final String audience;
    @Getter
    private final long accessTokenTtlSeconds;

    public JwtServiceImpl(RSAPrivateKey privateKey,
                          RSAPublicKey pubicKey,
                          @Value("${spring.auth.jwt.issuer}") String issuer,
                          @Value("${spring.auth.jwt.audience}") String audience,
                          @Value("${spring.auth.jwt.access-token-ttl-seconds}") long accessTokenTtlSeconds) {
        this.privateKey = privateKey;
        this.pubicKey = pubicKey;
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().toList())
                .signWith(privateKey)
                .compact();
    }

    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(pubicKey)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_EXPIRED, "Access token has expired");
        } catch (SignatureException e) {
            log.warn("JWT invalid signature: {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_INVALID, "Invalid token signature");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            log.warn("JWT malformed: {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_INVALID, "Malformed token");
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_INVALID, "Token validation failed");
        }
    }

    public Set<String> extractRoles(Claims claims) {
        Object raw = claims.get("roles");
        if (raw instanceof List<?> list) {
            return Set.copyOf(list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList());
        }
        return Set.of();
    }
}
