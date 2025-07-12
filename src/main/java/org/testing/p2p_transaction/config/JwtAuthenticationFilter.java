package org.testing.p2p_transaction.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;  // <-- добавь этот импорт
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;  // <-- исправь импорт
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Получаем токен из заголовка Authorization
        String header = request.getHeader("Authorization");

        // Проверяем, что токен есть и начинается с "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Убираем "Bearer " из строки

            // Проверяем токен на валидность
            if (jwtTokenProvider.validateToken(token)) {
                // Если валидный — получаем username из токена
                String username = jwtTokenProvider.getUsernameFromToken(token);

                // Создаём UserDetails на основе username (здесь ты можешь получить из БД)
                // Для примера создаём объект с ролями из токена
                List<GrantedAuthority> authorities = jwtTokenProvider.getRolesFromToken(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        username,
                        "", // пароль не нужен, т.к. у нас аутентификация по токену
                        authorities);

                // Создаём объект аутентификации
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                // Передаём в контекст безопасности Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}
