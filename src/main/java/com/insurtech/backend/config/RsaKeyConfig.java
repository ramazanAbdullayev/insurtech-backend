package com.insurtech.backend.config;

import com.insurtech.backend.security.AuthProperties;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RsaKeyConfig {

  private final AuthProperties authProperties;

  @Bean
  public RSAPrivateKey rsaPrivateKey() throws Exception {
    byte[] bytes =
        Base64.getMimeDecoder().decode(stripPemHeaders(authProperties.jwt().privateKeyBase64()));
    return (RSAPrivateKey)
        KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
  }

  @Bean
  public RSAPublicKey rsaPublicKey() throws Exception {
    byte[] bytes =
        Base64.getMimeDecoder().decode(stripPemHeaders(authProperties.jwt().publicKeyBase64()));
    return (RSAPublicKey)
        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
  }

  private String stripPemHeaders(String pem) {
    return pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
  }
}
