package org.testing.p2p_transaction.exception;

public class CantSaveUserException extends RuntimeException {
    public CantSaveUserException() {
        super("Ошибка при сохранении убедитесь что почта и userName уникальны");
    }
}
