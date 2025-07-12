package org.testing.nft_transaction.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor

@Table(schema = "p2p", name = "user")
public class User {
    @Id
    private UUID id;

    private String mail;
    private String userName;
    private String password;
    private String fullName;
    private LocalDateTime timestamp;
}
