package org.testing.p2p_transaction.dto;

import lombok.Data;



@Data
public class UserRegistrationDto {
    private String mail;
    private String userName;
    private String password;
    private String fullName;
}
