package com.insurtech.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    @Value("${spring.auth.jwt.private-key-base64}")
    private String privateKeyBase64;

    @Value("${spring.auth.jwt.public-key-base64}")
    private String publicKeyBase64;

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        byte[] bytes = Base64.getMimeDecoder().decode(stripPemHeaders(privateKeyBase64));
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        byte[] bytes = Base64.getMimeDecoder().decode(stripPemHeaders(publicKeyBase64));
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(bytes));
    }

    private String stripPemHeaders(String pem) {
        return pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
    }
}
