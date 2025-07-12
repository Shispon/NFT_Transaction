package org.testing.p2p_transaction.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Getter
    @Value("${spring.jwt.expiration}")
    private long expiration;

    public byte[] getSecretBytes() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

}



