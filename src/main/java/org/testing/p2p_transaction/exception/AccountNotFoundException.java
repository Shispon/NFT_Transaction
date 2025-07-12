package org.testing.p2p_transaction.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String account) {
        super("счета " + account + " не найден");
    }
}