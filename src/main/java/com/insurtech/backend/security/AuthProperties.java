package com.insurtech.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(String[] publicPaths, Jwt jwt) {
  public record Jwt(
      String issuer,
      String audience,
      long accessTokenTtlSeconds,
      long refreshTokenTtlDays,
      String privateKeyBase64,
      String publicKeyBase64) {}
}
