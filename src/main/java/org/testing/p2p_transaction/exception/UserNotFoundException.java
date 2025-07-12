package org.testing.p2p_transaction.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("Пользователь с id " + userId + " не найден");
    }
}

