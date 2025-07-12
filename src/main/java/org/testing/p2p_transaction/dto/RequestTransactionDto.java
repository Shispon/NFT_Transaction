package org.testing.p2p_transaction.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestTransactionDto {
    private UUID userId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Double amount;
}
