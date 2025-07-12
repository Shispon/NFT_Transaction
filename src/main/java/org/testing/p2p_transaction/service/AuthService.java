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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse auth(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUserName(), authRequest.getPassword())
        );

        User user = userRepository.findByUserName(authRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Генерируем токен через JwtTokenProvider (на основе Spring Security JwtEncoder)
        String token = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());

        return new AuthResponse(token, user.getId(), user.getFullName());
    }
}

