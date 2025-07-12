package org.testing.p2p_transaction.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class AccountResponseDto {
    private double balance;
    private UUID userId;
}
