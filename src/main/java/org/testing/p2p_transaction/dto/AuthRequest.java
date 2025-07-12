package org.testing.p2p_transaction.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String userName;
    private String password;
}