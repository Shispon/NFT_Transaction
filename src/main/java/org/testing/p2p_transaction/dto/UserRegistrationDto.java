package org.testing.p2p_transaction.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserRegistrationDto {
    private String mail;
    private String userName;
    private String password;
    private String fullName;
}
