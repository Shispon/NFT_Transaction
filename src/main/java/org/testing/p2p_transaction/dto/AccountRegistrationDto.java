package org.testing.p2p_transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRegistrationDto {
    private String accountNumber;
    private double balance;
}
