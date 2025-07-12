package org.testing.p2p_transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testing.p2p_transaction.dto.UserRegistrationDto;
import org.testing.p2p_transaction.dto.UserResponseDto;
import org.testing.p2p_transaction.entity.User;
import org.testing.p2p_transaction.exception.CantSaveUserException;
import org.testing.p2p_transaction.repository.UserRepository;
import org.testing.p2p_transaction.util.HashPassword;

import java.time.LocalDateTime;

/**
 * Сервис для управления пользователями системы.
 * Содержит бизнес-логику регистрации пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Регистрирует нового пользователя.
     *
     * @param registrationDto объект {@link UserRegistrationDto}, содержащий почту, имя пользователя, пароль и ФИО.
     * @return объект {@link UserResponseDto} с данными зарегистрированного пользователя.
     * @throws CantSaveUserException если почта или имя пользователя уже существуют в системе.
     */
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        boolean emailExists = userRepository.existsByMail(registrationDto.getMail());
        boolean userNameExists = userRepository.existsByUserName(registrationDto.getUserName());

        if (emailExists || userNameExists) {
            throw new CantSaveUserException();
        }

        User user = new User();
        user.setMail(registrationDto.getMail());
        user.setUserName(registrationDto.getUserName());
        user.setPassword(HashPassword.hash(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getMail(),
                savedUser.getUserName(),
                savedUser.getFullName(),
                savedUser.getCreatedAt()
        );
    }
}

