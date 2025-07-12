package org.testing.p2p_transaction.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountNumber) {
        super("Недостаточно средств на счете: " + accountNumber);
    }
}
