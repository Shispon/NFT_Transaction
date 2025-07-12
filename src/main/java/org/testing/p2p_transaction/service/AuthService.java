package org.testing.p2p_transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.testing.p2p_transaction.config.JwtTokenProvider;
import org.testing.p2p_transaction.dto.AuthRequest;
import org.testing.p2p_transaction.dto.AuthResponse;
import org.testing.p2p_transaction.entity.User;
import org.testing.p2p_transaction.repository.UserRepository;

/**
 * Сервис авторизации пользователей.
 * Обрабатывает аутентификацию и генерацию JWT-токенов.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT-токен при успешной авторизации.
     *
     * @param authRequest объект {@link AuthRequest}, содержащий имя пользователя и пароль.
     * @return объект {@link AuthResponse}, содержащий JWT-токен, ID пользователя и его полное имя.
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден.
     */
    public AuthResponse auth(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUserName(), authRequest.getPassword())
        );

        User user = userRepository.findByUserName(authRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());

        return new AuthResponse(token, user.getId(), user.getFullName());
    }
}

