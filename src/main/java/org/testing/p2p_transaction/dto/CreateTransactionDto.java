package org.testing.p2p_transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CreateTransactionDto {
    private String fromAccountNumber;
    private String toAccountNumber;
    private Double amount;
}
