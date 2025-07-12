package org.testing.p2p_transaction.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    // Генерация токена JWT
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(jwtConfig.getExpiration()));

        // Список ролей пользователя в виде строк
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder().setIssuer("your-app").setSubject(userDetails.getUsername()).claim("roles", roles).setIssuedAt(issuedAt).setExpiration(expiration).signWith(Keys.hmacShaKeyFor(jwtConfig.getSecretBytes()), SignatureAlgorithm.HS256).compact();
    }

    // Метод проверки токена (валидность и срок действия)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretBytes())).build().parseClaimsJws(token);
            return true; // если парсинг прошёл без ошибок — токен валиден
        } catch (JwtException | IllegalArgumentException e) {
            return false; // токен не валиден
        }
    }

    // Получение username из токена
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretBytes())).build().parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    // Получение ролей из токена (если нужно)
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecretBytes())).build().parseClaimsJws(token).getBody();

        return claims.get("roles", List.class);
    }
}

